package pro.themed.perappdownscale

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.edit

// Product IDs - update these to match your Play Console setup

const val LICENSE = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA8IMez16zFwQiheoVlLTxeX5itR4dZiRLr63Ws0vFLEi58JR4Q9sXyq0jgzdpXQ7yQrpn54HKnJah4FZRz5hx9i/tPsvH7UVmXHf/qAwi/y90491c5XAx1hdFnmSQ71x+1f9L4zTY3jAmdd9fkmlN0J1wFHJeK1uylLjFW15z/tgcrddryv9VVRXg4g4nkUrhtMIafueYNjw8Tb10a9cF+9q7hCgQH9711WJV3QAe/1Mi04tP/JEkqqTkRmPQB4aM1TZtX0VUGvACJSThlDidxhm0wh4F2sBKtP25a10l0fmmWwG40Va67M2+EJ18J5brcdxkx8pjf4832Cg5r0ZZ+QIDAQAB"

const val PRODUCT_ADFREE = "adfree_upgrade"
val PRODUCT_TIPS = listOf("tip_1") // Single tip product with quantity selection

// Purchase state data class
data class PurchaseState(
    val isAdFreePurchased: Boolean = false,
    val adFreePurchaseDate: String? = null,
    val adFreePurchaseToken: String? = null,
    val availableProducts: List<ProductDetails> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// Tip product details
data class TipProduct(
    val amount: Int,
    val productDetails: ProductDetails
)

class BillingManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    private lateinit var billingClient: BillingClient
    private val scope = CoroutineScope(Dispatchers.IO)
    private var isConnecting = false // Prevent multiple simultaneous connection attempts

    // State flow for reactive UI updates
    private val _purchaseState = MutableStateFlow(PurchaseState())
    val purchaseState: StateFlow<PurchaseState> = _purchaseState

    // Purchase update listener
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        scope.launch {
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    purchases?.forEach { purchase ->
                        handlePurchase(purchase)
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    updateState { copy(errorMessage = "Purchase cancelled") }
                }
                else -> {
                    updateState { copy(errorMessage = "Purchase failed: ${billingResult.debugMessage}") }
                }
            }
        }
    }

    // Billing client state listener
    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            isConnecting = false // Reset connection flag
            Log.d(TAG, "Billing setup finished: ${billingResult.responseCode} - ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Billing client ready, querying products...")
                queryAvailableProducts()
                queryPurchases()
            } else {
                Log.e(TAG, "Billing setup failed: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                updateState { copy(errorMessage = "Billing setup failed: ${billingResult.debugMessage}") }
            }
        }

        override fun onBillingServiceDisconnected() {
            Log.w(TAG, "Billing service disconnected, retrying...")
            startConnection()
        }
    }

    init {
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    fun startConnection() {
        if (!billingClient.isReady && !isConnecting) {
            isConnecting = true
            Log.d(TAG, "Starting billing client connection...")
            billingClient.startConnection(billingClientStateListener)
        } else if (isConnecting) {
            Log.d(TAG, "Connection already in progress, skipping...")
        }
    }

    fun ensureBillingReady(): Boolean {
        return if (billingClient.isReady) {
            true
        } else {
            // Start connection if not already connecting
            if (!isConnecting) {
                startConnection()
            }
            false
        }
    }

    fun refreshProductsIfReady() {
        if (billingClient.isReady && !isConnecting) {
            Log.d(TAG, "Refreshing products (client ready)")
            queryAvailableProducts()
        } else {
            Log.d(TAG, "Skipping product refresh (client not ready or connecting)")
        }
    }

    private fun queryAvailableProducts() {
        updateState { copy(isLoading = true) }

        val productList = (listOf(PRODUCT_ADFREE) + PRODUCT_TIPS).map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            scope.launch {
                Log.d(TAG, "Query result: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                Log.d(TAG, "Queried products: ${(listOf(PRODUCT_ADFREE) + PRODUCT_TIPS).joinToString()}")

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Found ${productDetailsList?.size ?: 0} products")
                    productDetailsList?.forEach { product ->
                        Log.d(TAG, "Product: ${product.productId} - ${product.name} - ${product.productType}")
                    }
                    updateState {
                        copy(
                            availableProducts = productDetailsList ?: emptyList(),
                            isLoading = false
                        )
                    }
                } else {
                    Log.e(TAG, "Failed to query products: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                    updateState {
                        copy(
                            errorMessage = "Failed to load products: ${billingResult.debugMessage}",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchases ->
            scope.launch {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchases.forEach { purchase ->
                        handlePurchase(purchase, updateState = false)
                    }
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase, updateState: Boolean = true) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val productId = purchase.products.firstOrNull()

            when {
                productId == PRODUCT_ADFREE -> {
                    // AdFree purchase - acknowledge it
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }

                    val purchaseDate = formatPurchaseDate(purchase.purchaseTime)

                    // Note: AdFree purchase status is managed separately from Firebase contributor status
                    Log.d(TAG, "AdFree purchase processed successfully")

                    if (updateState) {
                        updateState {
                            copy(
                                isAdFreePurchased = true,
                                adFreePurchaseDate = purchaseDate,
                                adFreePurchaseToken = purchase.purchaseToken
                            )
                        }
                    }
                }
                productId in PRODUCT_TIPS -> {
                    // Tip purchase - consume it immediately
                    consumePurchase(purchase)
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged: ${purchase.products.firstOrNull()}")
            } else {
                Log.e(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
            }
        }
    }

    private fun consumePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase consumed: ${purchase.products.firstOrNull()}")
            } else {
                Log.e(TAG, "Failed to consume purchase: ${billingResult.debugMessage}")
            }
        }
    }

    fun launchAdFreePurchase(activity: Activity) {
        val adFreeProduct = purchaseState.value.availableProducts
            .find { it.productId == PRODUCT_ADFREE }

        if (adFreeProduct != null) {
            launchPurchase(activity, adFreeProduct)
        } else {
            updateState { copy(errorMessage = "AdFree product not available") }
        }
    }

    fun launchTipPurchase(activity: Activity, tipAmount: Int = 1) {
        // Always use tip_1 product with quantity selection enabled in Play Console
        val tipProduct = purchaseState.value.availableProducts
            .find { it.productId == "tip_1" }

        if (tipProduct != null) {
            launchPurchase(activity, tipProduct)
        } else {
            updateState { copy(errorMessage = "Tip product not available") }
        }
    }

    private fun launchPurchase(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    fun getTipProducts(): List<TipProduct> {
        return purchaseState.value.availableProducts
            .filter { it.productId in PRODUCT_TIPS }
            .mapNotNull { productDetails ->
                val amount = productDetails.productId.removePrefix("tip_").toIntOrNull()
                amount?.let { TipProduct(it, productDetails) }
            }
            .sortedBy { it.amount }
    }

    fun checkPurchasesWithinTimeout(timeoutMillis: Long = 10000) {
        scope.launch {
            Log.d(TAG, "Starting purchase checks within ${timeoutMillis}ms timeout")

            val startTime = System.currentTimeMillis()
            val endTime = startTime + timeoutMillis
            var checkCount = 0

            // First, establish connection and query products immediately
            if (!billingClient.isReady) {
                Log.d(TAG, "Check ${++checkCount}: Billing client not ready, establishing connection")
                startConnection()
                delay(1000) // Wait 1 second for connection
            }

            // Query products first (needed for purchase flow)
            if (billingClient.isReady) {
                Log.d(TAG, "Check ${++checkCount}: Querying available products")
                queryAvailableProducts()
                delay(500) // Brief pause
            }

            // Rapid purchase checks within timeout
            while (System.currentTimeMillis() < endTime) {
                if (billingClient.isReady && !isConnecting) {
                    Log.d(TAG, "Check ${++checkCount}: Querying purchases (${(endTime - System.currentTimeMillis())}ms remaining)")
                    queryPurchases()
                } else if (!billingClient.isReady && !isConnecting) {
                    Log.d(TAG, "Check ${++checkCount}: Billing client not ready, attempting connection")
                    startConnection()
                } else {
                    Log.d(TAG, "Check ${++checkCount}: Billing client busy (connecting=${isConnecting}, ready=${billingClient.isReady})")
                }

                // Check every 1-2 seconds within the timeout
                val remainingTime = endTime - System.currentTimeMillis()
                val delayTime = minOf(2000L, maxOf(500L, remainingTime / 2))
                delay(delayTime)
            }

            // Note: Billing system no longer manages shared preferences isContributor flag
            // Firebase contributor status is handled separately
            Log.d(TAG, "Purchase check completed - AdFree purchased: ${purchaseState.value.isAdFreePurchased}")

            Log.d(TAG, "Purchase checks completed after ${System.currentTimeMillis() - startTime}ms (${checkCount} checks)")
            Log.d(TAG, "Final state - AdFree purchased: ${purchaseState.value.isAdFreePurchased}")
        }
    }

    fun refreshProducts() {
        Log.d(TAG, "Manual product refresh requested")
        if (billingClient.isReady) {
            queryAvailableProducts()
        } else {
            startConnection()
        }
    }

    fun clearError() {
        updateState { copy(errorMessage = null) }
    }

    private fun updateState(update: PurchaseState.() -> PurchaseState) {
        _purchaseState.value = _purchaseState.value.update()
    }

    private fun formatPurchaseDate(purchaseTime: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(purchaseTime))
    }

    companion object {
        private const val TAG = "BillingManager"
    }
}
