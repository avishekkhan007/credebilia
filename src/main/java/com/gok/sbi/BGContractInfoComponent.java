package com.gok.sbi;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import com.gok.sbi.credebilia.model.BGFileModel;
import com.gok.sbi.credebilia.service.FileTransferService;
import com.gok.sbi.credebilia.service.ParseBGFileService;
import com.gok.sbi.credebilia.service.QuorumService;
import com.gok.sbi.credebilia.service.RestService;
import com.gok.sbi.credebilia.web3j.BankGuaranteeManager;

@Component
public class BGContractInfoComponent implements CommandLineRunner {

	@Autowired
	private FileTransferService fileTransferService;
	@Autowired
	private RestService restService;

	@Autowired
	private ParseBGFileService parseService;

	@Autowired
	private QuorumService quorumService;

	private Logger logger = LoggerFactory.getLogger(BGContractInfoComponent.class);

	// Rest call variables
	@Value("${bg.file.remote.path}")
	private String remoteFilePath;
	@Value("${bg.file.local.path}")
	private String localFilepath;
	@Value("${com.gok.sbi.rest.upload.url}")
	private String restUrl;
	@Value("${com.gok.sbi.rest.host}")
	private String resthost;
	@Value("${com.gok.sbi.rest.apiKey}")
	private String restApiKey;

	// BG variables for Blockchain
	@Value("${com.gok.sbi.uid}")
	private String blkchainUid;
	@Value("${com.gok.sbi.bankgid}")
	private String blkChainbankGid;
	@Value("${com.gok.sbi.payment.advice.number}")
	private String blkChainPaymentAdviceNumber;
	@Value("${com.gok.sbi.payment.advice.uid}")
	private String blkChainPaymentAdviceUid;
	@Value("${com.gok.sbi.reference.number}")
	private String blkChainReferenceNumber;
	@Value("${com.gok.sbi.branch.name}")
	private String blkChainBrancName;
	@Value("${com.gok.sbi.bank.representative.name}")
	private String blkChainBrancRepresentativeName;
	@Value("${com.gok.sbi.insurance.date}")
	private Integer blkChainInsuranceDate;
	@Value("${com.gok.sbi.validity.period}")
	private Integer blkChainValidityPeriod;
	@Value("${com.gok.sbi.claim.expiry.date}")
	private Integer blkChainClaimExpiryDate;
	@Value("${com.gok.sbi.valid.from}")
	private Integer blkChainValidFrom;
	@Value("${com.gok.sbi.valid.till}")
	private Integer blkChainValidTill;
	@Value("${com.gok.sbi.currency}")
	private String blkChainCurrency;
	@Value("${com.gok.sbi.amount}")
	private Integer blkChainAmount;
	@Value("${com.gok.sbi.benificery.name}")
	private String blkChainBenificeryName;
	@Value("${com.gok.sbi.filehash}")
	private String blkChainFileHash;
	@Value("${com.gok.sbi.bank.encrypted.key}")
	private String blkChainBankEncryptedKey;
	@Value("${com.gok.sbi.egp.encrypted.key}")
	private String blkChainEgpEncryptedKey;
	@Value("${com.gok.sbi.vendor.encrypted.key}")
	private String blkChainVendorEncryptedKey;

	@Value("${com.gok.sbi.sftp.isLocal}")
	private boolean isLocalBGFile;

	@Override
	public void run(String... args) throws Exception {

		logger.info("Start download file");
		boolean isDownloaded = false;
		if (!isLocalBGFile) {
			isDownloaded = fileTransferService.downloadFile(localFilepath, remoteFilePath);
		} else {
			isDownloaded = fileTransferService.copyFromOneFolderToAnother(localFilepath, remoteFilePath);
		}
		logger.info("Download result: " + String.valueOf(isDownloaded));

		logger.info("Start parsing the  file");
		BGFileModel bgmetadatModel = parseService.parseBGFile(localFilepath);
		logger.info("finished parsing the  file" + bgmetadatModel.toString());

		logger.info("Start uploading rest file");

		String result = restService.postFileWithApiKeyAuth(localFilepath, resthost, restUrl, restApiKey);

		logger.info("Finished uploading to rest " + result);

		logger.info("Start Blockchain publish BG to smart contracts");

		BankGuaranteeManager.BankGuarantee bg = populateSampleBankGuarantee(blkchainUid, blkChainbankGid,
				blkChainPaymentAdviceNumber, blkChainPaymentAdviceUid, blkChainReferenceNumber, blkChainBrancName,
				blkChainBrancRepresentativeName, blkChainInsuranceDate, blkChainValidityPeriod, blkChainClaimExpiryDate,
				blkChainValidFrom, blkChainValidTill, blkChainCurrency, blkChainAmount, blkChainBenificeryName,
				blkChainFileHash, blkChainBankEncryptedKey, blkChainEgpEncryptedKey, blkChainVendorEncryptedKey);

		TransactionReceipt transactionReceipt = quorumService.publishBankGuarantee(bg);

		logger.info("End Blockchain publish BG to smart contracts");

		logger.info("The transaction receipt received from publishing the BG :::" + transactionReceipt.toString());

	}

	private BankGuaranteeManager.BankGuarantee populateSampleBankGuarantee(String buid, String bGid,
			String payAdvNumber, String payAdvUid, String refNumber, String branchNm, String branchRepNm,
			Integer IssueDt, Integer valPeriod, Integer clmExpDt, Integer validFrm, Integer validTl, String curr,
			Integer amt, String benificeryNm, String fileHsh, String bankEncrKey, String egpEncrKey,
			String vendrEncrKey) {
		byte[] uid = Numeric.hexStringToByteArray(buid);
		String bankGid = bGid;
		String paymentAdviceNumber = payAdvNumber;
		byte[] paymentAdviceUid = Numeric.hexStringToByteArray(payAdvUid);
		String referenceNumber = refNumber;
		String branchName = branchNm;
		String bankRepresentativeName = branchRepNm;
		BigInteger issuanceDate = BigInteger.valueOf(IssueDt);
		BigInteger validityPeriod = BigInteger.valueOf(valPeriod);
		BigInteger claimExpiryDate = BigInteger.valueOf(clmExpDt);
		BigInteger validFrom = BigInteger.valueOf(validFrm);
		BigInteger validTill = BigInteger.valueOf(validTl);
		String currency = curr;
		BigInteger amount = BigInteger.valueOf(amt);
		String beneficiaryName = benificeryNm;
		String fileHash = fileHsh;
		String bankEncryptedKey = bankEncrKey;
		String egpEncryptedKey = egpEncrKey;
		String vendorEncryptedKey = vendrEncrKey;
		BigInteger amendment = BigInteger.ZERO;
		BigInteger version = BigInteger.ONE;

		BankGuaranteeManager.BankGuarantee bankGuarantee = new BankGuaranteeManager.BankGuarantee(uid, bankGid,
				paymentAdviceNumber, paymentAdviceUid, referenceNumber, branchName, bankRepresentativeName,
				issuanceDate, validityPeriod, claimExpiryDate, validFrom, validTill, currency, amount, beneficiaryName,
				fileHash, bankEncryptedKey, egpEncryptedKey, vendorEncryptedKey, amendment, version);
		return bankGuarantee;
	}

}
