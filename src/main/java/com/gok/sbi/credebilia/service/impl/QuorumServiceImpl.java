package com.gok.sbi.credebilia.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;
import org.web3j.tx.gas.ContractGasProvider;

import com.gok.sbi.credebilia.service.QuorumService;
import com.gok.sbi.credebilia.web3j.BankGuaranteeManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuorumServiceImpl implements QuorumService {


    @Value("${com.gok.sbi.https.node.url}")
    private String httpsNodeUrl;


    @Value("${com.gok.sbi.ws.node.url}")
    private String wsNodeUrl;

    @Value("${com.gok.sbi.wallet.path}")
    private String walletFolder;

    @Value("${com.gok.sbi.wallet.password}")
    private String walletPassword;

    @Value("${com.gok.sbi.wallet.file.name}")
    private String walletFileName;

    @Value("${com.gok.sbi.wallet.private.key}")
    private String walletPrivateKey;

    @Value("${com.gok.sbi.bankGuaranteeManager.contract.address}")
    private String BankGuaranteeManagerContractAddress;

    @Autowired
    private ResourceLoader resourceLoader;

    private Resource resource;
    private File walletFile;

    @PostConstruct
    public void init() {
        walletFile = new File(walletFolder+walletFileName);
    }

    private Quorum getQuorumObject() {
        return Quorum.build(new HttpService(httpsNodeUrl));
    }

    public Credentials getCredentialFromWallet() {
        try {
            return WalletUtils.loadCredentials(walletPassword, walletFile);
        } catch (IOException | CipherException e) {
            log.error("Could not load wallet.",e);
            throw new RuntimeException("Could not load wallet. Original error : "+e.getMessage(), e);
        }
    }

    public  TransactionReceipt publishBankGuarantee(BankGuaranteeManager.BankGuarantee bankGuarantee) {
        Date startTime = new Date();
        ContractGasProvider contractGasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String s) {
                return BigInteger.ZERO;
            }

            @Override
            public BigInteger getGasPrice() {
                return BigInteger.ZERO;
            }

            @Override
            public BigInteger getGasLimit(String s) {
                return BigInteger.valueOf(470000000l);
            }

            @Override
            public BigInteger getGasLimit() {
                return BigInteger.valueOf(470000000l);
            }
        };
        BankGuaranteeManager bankGuaranteeManager = BankGuaranteeManager.load(BankGuaranteeManagerContractAddress,
                getQuorumObject(), getCredentialFromWallet(), contractGasProvider);
        try {
            /* Tuple3<List<BankGuaranteeManager.BankGuarantee>, BigInteger, BigInteger> response
                    = BankGuaranteeManager.getBankGuaranteesByPaymentAdviceRefAndEgpId("par014", "EGP_BLOCKCHAIN.state3egp", BigInteger.ONE, BigInteger.ONE).send();
          */
            TransactionReceipt transactionReceipt = bankGuaranteeManager.publishBankGuarantee(bankGuarantee).send();
            log.info("TransactionReceipt: {}, Transaction Hash: {}, Block Number: {}",transactionReceipt, transactionReceipt.getTransactionHash(), transactionReceipt.getBlockNumber());
            Date endTime = new Date();
            log.info("Start Time - {}, End Time {}. Time Taken {}", startTime, endTime, endTime.getTime()-startTime.getTime());
            return transactionReceipt;
        } catch (Exception e) {
            log.error("Could not get bank guarantee.",e);
            throw new RuntimeException("Could not get bank guarantee. Original error : "+e.getMessage(), e);
        }
    }

    /*
    public void createWallet() {
        try {
            WalletUtils.generateFullNewWalletFile(walletPassword, new File(walletFolder));
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CipherException | IOException e) {
            log.error("Could not generate wallet.",e);
            throw new RuntimeException("Could not generate wallet. Original error : "+e.getMessage(), e);
        }
    }

    public Credentials getCredentialFromPrivateKey() {
            return Credentials.create(walletPrivateKey);
    }

    private BankGuaranteeManager.BankGuarantee populateSampleBankGuarantee() {
        byte[] uid = Numeric.hexStringToByteArray("0x0000000000000000");
        String bankGid ="UXB4YW5L";
        String paymentAdviceNumber = "par015";
        byte[] paymentAdviceUid = Numeric.hexStringToByteArray("0x92d196cb102f8946");
        String referenceNumber = "bg01";
        String branchName = "RT Nagar";
        String bankRepresentativeName = "Rina";
        BigInteger issuanceDate = BigInteger.valueOf(1515436200);
        BigInteger validityPeriod =BigInteger.valueOf(258);
        BigInteger claimExpiryDate = BigInteger.valueOf(1767983400);
        BigInteger validFrom = BigInteger.valueOf(1578594600);
        BigInteger validTill = BigInteger.valueOf(1767983400);
        String currency ="INR";
        BigInteger amount =BigInteger.valueOf(2500000);
        String beneficiaryName ="KEB";
        String fileHash = "c7a18a67d24f4dde6e95b922dde6c8730b9ee96a26106124bf77059e7ead8b51";
        String bankEncryptedKey = "";
        String egpEncryptedKey ="";
        String vendorEncryptedKey = "";
        BigInteger amendment = BigInteger.ZERO;
        BigInteger version = BigInteger.ONE;


        BankGuaranteeManager.BankGuarantee bankGuarantee =
                new BankGuaranteeManager.BankGuarantee(uid, bankGid, paymentAdviceNumber, paymentAdviceUid,
                        referenceNumber, branchName, bankRepresentativeName, issuanceDate, validityPeriod,
                        claimExpiryDate, validFrom, validTill, currency, amount, beneficiaryName, fileHash,
                        bankEncryptedKey, egpEncryptedKey, vendorEncryptedKey, amendment, version);
        return bankGuarantee;
    }
    */

}
