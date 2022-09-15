package com.adyen.android.assignment

import com.adyen.android.assignment.money.Change
import kotlin.math.min

/**
 * The CashRegister class holds the logic for performing transactions.
 *
 * @param change The change that the CashRegister is holding.
 */
class CashRegister(private val change: Change) {
    /**
     * Performs a transaction for a product/products with a certain price and a given amount.
     *
     * @param price The price of the product(s).
     * @param amountPaid The amount paid by the shopper.
     *
     * @return The change for the transaction.
     *
     * @throws TransactionException If the transaction cannot be performed.
     */
    fun performTransaction(price: Long, amountPaid: Change): Change {
        // Handle basic transaction validations
        return when {
            price < 1 -> throw TransactionException("The price of product is less than 1")
            amountPaid.total < price ->
                throw TransactionException("The amount paid by is less than product's price")
            amountPaid.total - price == 0L -> Change.none()
            else -> collectAmountReturnChange(amountPaid, amountPaid.total - price)
        }
    }

    /**
     * Add the given change to the cash register
     * and return the minimal amount of change if needed
     *
     * @param toCollect the change to be added to the cash register
     * @param amountToReturn the amount that need to be converted to change
     * @return the minimal amount of change if needed
     */
    private fun collectAmountReturnChange(toCollect: Change, amountToReturn: Long): Change {
        change.add(toCollect)
        val amountOfChangeToGive = Change()
        var amountToFind = amountToReturn
        val monetaryElementsAvailable = change.getElements()
        var i = 0
        while (amountToFind != 0L && i < monetaryElementsAvailable.size) {
            val currentMonetaryElement = monetaryElementsAvailable.elementAt(i)
            val monetaryElementCount =
                amountToFind.toDouble() / currentMonetaryElement.minorValue.toDouble()
            if (monetaryElementCount >= 1) {
                val elementCountToWithdraw =
                    min(monetaryElementCount.toInt(), change.getCount(currentMonetaryElement))
                amountOfChangeToGive.add(currentMonetaryElement, elementCountToWithdraw)
                change.remove(currentMonetaryElement, elementCountToWithdraw)
                amountToFind -= elementCountToWithdraw * currentMonetaryElement.minorValue
            }
            i++
        }
        if (amountToFind == 0L) return amountOfChangeToGive
        else throw TransactionException("Not enough change available")
    }

    class TransactionException(message: String, cause: Throwable? = null) :
        Exception(message, cause)
}
