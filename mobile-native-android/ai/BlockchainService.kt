// ============================================
// 🚀 Edham Logistics - Blockchain Service
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.security.MessageDigest
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ============================================
 * Blockchain Service - خدمة البلوك تشين
 * ============================================
 * توثيق المعاملات في سلسلة البلوك تشين لضمان عدم التلاعب
 */

@Singleton
class BlockchainService @Inject constructor() {
    
    private val blockchain = mutableListOf<Block>()
    private val pendingTransactions = mutableListOf<Transaction>()
    
    /**
     * إنشاء معاملة جديدة
     */
    fun createTransaction(
        from: String,
        to: String,
        data: Map<String, Any>,
        transactionType: TransactionType
    ): Transaction {
        val transaction = Transaction(
            id = generateTransactionId(),
            from = from,
            to = to,
            data = data,
            type = transactionType,
            timestamp = Instant.now().toEpochMilli(),
            status = TransactionStatus.PENDING
        )
        
        pendingTransactions.add(transaction)
        return transaction
    }
    
    /**
     * تعدين كتلة جديدة
     */
    fun mineBlock(minerAddress: String): Block {
        val transactionsToMine = pendingTransactions.toList()
        pendingTransactions.clear()
        
        val previousBlock = blockchain.lastOrNull()
        val newBlock = Block(
            index = blockchain.size,
            timestamp = Instant.now().toEpochMilli(),
            transactions = transactionsToMine,
            previousHash = previousBlock?.hash ?: "0",
            nonce = 0,
            miner = minerAddress
        )
        
        // Proof of Work - تعدين الكتلة
        val minedBlock = mineBlockWithProofOfWork(newBlock)
        blockchain.add(minedBlock)
        
        return minedBlock
    }
    
    /**
     * تعدين الكتلة باستخدام Proof of Work
     */
    private fun mineBlockWithProofOfWork(block: Block): Block {
        val difficulty = 4 // عدد الأصفار المطلوبة في بداية الهاش
        var minedBlock = block
        
        while (!minedBlock.hash.startsWith("0".repeat(difficulty))) {
            minedBlock = minedBlock.copy(nonce = minedBlock.nonce + 1)
            minedBlock = minedBlock.copy(hash = calculateBlockHash(minedBlock))
        }
        
        return minedBlock
    }
    
    /**
     * التحقق من صحة البلوك تشين
     */
    fun validateBlockchain(): ValidationResult {
        for (i in 1 until blockchain.size) {
            val currentBlock = blockchain[i]
            val previousBlock = blockchain[i - 1]
            
            // التحقق من صحة الهاش
            if (currentBlock.hash != calculateBlockHash(currentBlock)) {
                return ValidationResult.Invalid("Invalid block hash at index $i")
            }
            
            // التحقق من ربط الكتل
            if (currentBlock.previousHash != previousBlock.hash) {
                return ValidationResult.Invalid("Broken chain link at index $i")
            }
        }
        
        return ValidationResult.Valid
    }
    
    /**
     * البحث عن معاملة
     */
    fun findTransaction(transactionId: String): Transaction? {
        // البحث في الكتل المعدنة
        blockchain.forEach { block ->
            val transaction = block.transactions.find { it.id == transactionId }
            if (transaction != null) return transaction
        }
        
        // البحث في المعاملات المعلقة
        return pendingTransactions.find { it.id == transactionId }
    }
    
    /**
     * الحصول على توازن العنوان
     */
    fun getBalance(address: String): Double {
        var balance = 0.0
        
        blockchain.forEach { block ->
            block.transactions.forEach { transaction ->
                when (transaction.type) {
                    TransactionType.SHIPMENT_PAYMENT,
                    TransactionType.INVOICE_SETTLEMENT -> {
                        if (transaction.to == address) {
                            balance += transaction.data["amount"] as Double
                        }
                        if (transaction.from == address) {
                            balance -= transaction.data["amount"] as Double
                        }
                    }
                    TransactionType.DOCUMENT_VERIFICATION -> {
                        // لا يؤثر على الرصيد
                    }
                }
            }
        }
        
        return balance
    }
    
    /**
     * الحصول on سجل المعاملات للعنوان
     */
    fun getTransactionHistory(address: String): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        
        blockchain.forEach { block ->
            block.transactions.forEach { transaction ->
                if (transaction.from == address || transaction.to == address) {
                    transactions.add(transaction)
                }
            }
        }
        
        return transactions.sortedByDescending { it.timestamp }
    }
    
    /**
     * الحصول على إحصائيات البلوك تشين
     */
    fun getBlockchainStats(): BlockchainStats {
        return BlockchainStats(
            totalBlocks = blockchain.size,
            totalTransactions = blockchain.sumOf { it.transactions.size },
            pendingTransactions = pendingTransactions.size,
            lastBlockTime = blockchain.lastOrNull()?.timestamp ?: 0,
            difficulty = 4,
            hashRate = calculateHashRate()
        )
    }
    
    /**
     * حساب معدل الهاش
     */
    private fun calculateHashRate(): Double {
        // محاكاة حساب معدل الهاش (Hash/second)
        return 1000.0 + (blockchain.size * 50)
    }
    
    /**
     * حساب هاش الكتلة
     */
    private fun calculateBlockHash(block: Block): String {
        val content = "${block.index}${block.timestamp}${block.previousHash}${block.nonce}" +
                block.transactions.joinToString("") { "${it.id}${it.from}${it.to}" }
        
        return generateHash(content)
    }
    
    /**
     * حساب هاش المعاملة
     */
    private fun calculateTransactionHash(transaction: Transaction): String {
        val content = "${transaction.id}${transaction.from}${transaction.to}${transaction.timestamp}" +
                transaction.data.entries.joinToString("") { "${it.key}${it.value}" }
        
        return generateHash(content)
    }
    
    /**
     * إنشاء هاش SHA-256
     */
    private fun generateHash(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * إنشاء معرف المعاملة
     */
    private fun generateTransactionId(): String {
        val timestamp = Instant.now().toEpochMilli()
        val random = (1000..9999).random()
        return "TX_${timestamp}_$random"
    }
    
    /**
     * إنشاء NFT للشحنة
     */
    fun createShipmentNFT(
        shipmentId: String,
        owner: String,
        metadata: ShipmentMetadata
    ): Transaction {
        val nftData = mapOf(
            "shipmentId" to shipmentId,
            "owner" to owner,
            "metadata" to metadata,
            "tokenType" to "NFT",
            "standard" to "ERC-721"
        )
        
        return createTransaction(
            from = "SYSTEM",
            to = owner,
            data = nftData,
            transactionType = TransactionType.DOCUMENT_VERIFICATION
        )
    }
    
    /**
     * نقل ملكية NFT
     */
    fun transferNFT(
        nftId: String,
        from: String,
        to: String
    ): Transaction? {
        val nftTransaction = findTransaction(nftId)
        
        if (nftTransaction?.data?.get("owner") == from) {
            val transferData = nftTransaction.data.toMutableMap()
            transferData["owner"] = to
            transferData["previousOwner"] = from
            transferData["transferTimestamp"] = Instant.now().toEpochMilli()
            
            return createTransaction(
                from = from,
                to = to,
                data = transferData,
                transactionType = TransactionType.DOCUMENT_VERIFICATION
            )
        }
        
        return null
    }
}

/**
 * ============================================
// Data Classes and Enums
// ============================================
 */

data class Block(
    val index: Int,
    val timestamp: Long,
    val transactions: List<Transaction>,
    val previousHash: String,
    val nonce: Int,
    val miner: String,
    val hash: String = ""
) {
    val isValid: Boolean
        get() = hash.isNotEmpty()
}

data class Transaction(
    val id: String,
    val from: String,
    val to: String,
    val data: Map<String, Any>,
    val type: TransactionType,
    val timestamp: Long,
    val status: TransactionStatus,
    val hash: String = ""
) {
    val isValid: Boolean
        get() = hash.isNotEmpty()
}

data class ShipmentMetadata(
    val trackingNumber: String,
    val origin: String,
    val destination: String,
    val temperature: Int,
    val weight: Double,
    val specialInstructions: String
)

data class BlockchainStats(
    val totalBlocks: Int,
    val totalTransactions: Int,
    val pendingTransactions: Int,
    val lastBlockTime: Long,
    val difficulty: Int,
    val hashRate: Double
)

enum class TransactionType {
    SHIPMENT_PAYMENT,
    INVOICE_SETTLEMENT,
    DOCUMENT_VERIFICATION
}

enum class TransactionStatus {
    PENDING,
    CONFIRMED,
    FAILED
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}
