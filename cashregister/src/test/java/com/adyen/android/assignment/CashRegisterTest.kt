package com.adyen.android.assignment

import com.adyen.android.assignment.money.Bill
import com.adyen.android.assignment.money.Change
import com.adyen.android.assignment.money.Coin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CashRegisterTest {

    @Test
    fun `test transaction with an amount equal to product price should return empty change`() {
        //Given
        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
        }
        val cashRegisterChange = Change.none()
        val cashRegister = CashRegister(cashRegisterChange)
        val expected = Change.none()

        //When
        val returnedChange = cashRegister.performTransaction(amountPaid.total, amountPaid)

        //Then
        assertEquals(expected, returnedChange)
    }

    @Test
    fun `test transaction returns correct change`() {
        //Given
        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.FIFTY_CENT, 1)
            add(Coin.FIFTY_CENT, 1)
        }
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
        }
        val cashRegister = CashRegister(cashRegisterChange)

        //When
        val returnedChange = cashRegister.performTransaction(1500L, amountPaid)

        //Then
        assertEquals(100L, returnedChange.total)
    }

    @Test
    fun `test transaction returns minimum amount of change`() {
        //Given
        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.FIFTY_CENT, 1)
            add(Coin.FIFTY_CENT, 1)
        }
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
        }
        val cashRegister = CashRegister(cashRegisterChange)
        val expected = Change().apply {
            add(Coin.ONE_EURO, 1)
        }

        //When
        val returnedChange = cashRegister.performTransaction(1500L, amountPaid)

        //Then
        assertEquals(expected, returnedChange)
    }

    @Test
    fun `test price less than 1 throws transaction exception`() {
        //Given
        val amountPaid = Change.none()
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
        }

        //When
        val cashRegister = CashRegister(cashRegisterChange)

        //Then
        assertThrows(CashRegister.TransactionException::class.java) {
            cashRegister.performTransaction(-1, amountPaid)
        }
    }

    @Test
    fun `test perform transaction should throw an exception when register change`() {
        //Given
        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.ONE_EURO, 1)
        }
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 1) }
        }
        val cashRegister = CashRegister(cashRegisterChange)

        //Then
        assertThrows(CashRegister.TransactionException::class.java) {
            cashRegister.performTransaction(1599L, amountPaid)
        }
    }

    @Test
    fun `test perform transaction with an amount below the product price throws transaction exception`() {
        //Given
        val amountPaid = Change.none()
        val cashRegister = CashRegister(Change.max())
        val productPrice = 1000L

        //Then
        assertThrows(CashRegister.TransactionException::class.java) {
            cashRegister.performTransaction(productPrice, amountPaid)
        }
    }
}
