package com.gok.sbi.credebilia.service;

import com.gok.sbi.credebilia.web3j.BankGuaranteeManager;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

public interface QuorumService {
    TransactionReceipt publishBankGuarantee(BankGuaranteeManager.BankGuarantee bankGuarantee);
}
