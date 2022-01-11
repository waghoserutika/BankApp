package com.springboot.bankapp.controller;

import java.security.Principal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.bankapp.dto.Transfer;
import com.springboot.bankapp.model.Transaction;
import com.springboot.bankapp.service.TransactionService;

@RestController
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	/*
	 * beneficiary(To) account no
	 * username: extract(FROM account no)
	 * amount
	 * {
	 * 	    toAccountNumber: "",
	 * 		amount: ""
	 * }  : request body
	 *  transfer?toAccountNumber=_____&username=_____&amount=___: request param
	 *  transfer/toAccountNumber/username/amount : path variable 
	 */ 
	
	@PostMapping("/transfer")
	public Transaction doTransfer(Principal principal, @RequestBody Transfer transfer) {
		String username = principal.getName();
		
		/*
		 * STEP 1:
		 * Fetch details of fromAccount. 
		 * 1.1 fetch fromAccountNumber from username
		 * 
		 * STEP 2:
		 * 2.1 DEBIT the amount from fromAccountNumber / update the balance
		 * 2.2 CREDIT the amount to toAccountNumber / update the balance 
		 * 
		 * STEP 3:
		 * 3.1 insert the entry of transfer in transaction table
		 */ 
		
		//1.1
		String fromAccountNumber = transactionService.fetchFromAccountNumber(username);
		
		//2.1 
		transactionService.updateBalance(fromAccountNumber, transfer.getAmount()); 
		//2.2
		transactionService.creditAmount(transfer.getToAccountNumber(), transfer.getAmount());
		
		//3.1
		Transaction transaction = new Transaction();
		transaction.setAccountFrom(fromAccountNumber);
		transaction.setAccountTo(transfer.getToAccountNumber());
		transaction.setAmount(transfer.getAmount());
		transaction.setOperationType("TRANSFER");
		transaction.setDateOfTransaction(new Date()); 
		
		return transactionService.saveTransaction(transaction);
		
		
	}
}
