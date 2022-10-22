package com.gok.sbi.credebilia.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import com.gok.sbi.credebilia.model.BGFileModel;
import com.gok.sbi.credebilia.service.ParseBGFileService;
import com.gok.sbi.credebilia.web3j.BankGuaranteeManager;

@Service
public class ParseBGFileServiceImpl implements ParseBGFileService {

	private Logger logger = LoggerFactory.getLogger(ParseBGFileServiceImpl.class);

	@Override
	public BGFileModel parseBGFile(String filepath) {
		BGFileModel bgMetadataModel = new BGFileModel();
		File file = new File(filepath);
		bgMetadataModel.setNameOfFile(file.getName());
		// **Parse the pdf file

		byte[] thePDFFileBytes;
		
		try {
			thePDFFileBytes = readFileAsBytes(filepath);
			PDDocument pddDoc = PDDocument.load(thePDFFileBytes);
			PDFTextStripper reader = new PDFTextStripper();
			String pageText = reader.getText(pddDoc);
			pddDoc.close();
			
			String[] pdfDataArr = pageText.split("\n");
			for (String k : pdfDataArr) {
				/// System.out.println("The valus is " + k);
				if (k.contains("GUARANTEE NUMBER")) {
					String[] guranteeNumArr = k.split(":");
					if (guranteeNumArr.length > 1) {
						String guranteeNum = guranteeNumArr[1].trim();
						bgMetadataModel.setGuranteeNumber(guranteeNum);
					}
				}

				if (k.contains("AMOUNT OF GUARANTEE")) {
					String[] amtGuranteeArr = k.split(":");
					if (amtGuranteeArr.length > 1) {
						String amtGurantee = amtGuranteeArr[1].trim();
						bgMetadataModel.setAmountOfGurantee(amtGurantee);
					}
				}

				if (k.contains("GUARANTEE COVER FROM")) {
					String[] guranteeCoverFromArr = k.split(":");
					if (guranteeCoverFromArr.length > 1) {
						String guranteeCoverFrom = guranteeCoverFromArr[1].trim();
						bgMetadataModel.setGuranteeCoverFrom(guranteeCoverFrom);
					}
				}

				if (k.contains("LAST DATE FOR LODGEMENT OF CLAIM")) {
					String[] lastDateOfClaimLodgementArr = k.split(":");
					if (lastDateOfClaimLodgementArr.length > 1) {
						String lastDateOfClaimLodgemen = lastDateOfClaimLodgementArr[1].trim();
						bgMetadataModel.setLastDateOfLodgementOfClaim(lastDateOfClaimLodgemen);
					}
				}
			}

		} catch (IOException e) {
			logger.error("Not able to process the pdf BG file ", e);
		} 
		logger.info("the parsed data is",bgMetadataModel.toString() );
		return bgMetadataModel;
	}

	private byte[] readFileAsBytes(String filePath) throws IOException {
		FileInputStream inputStream = new FileInputStream(filePath);
		return IOUtils.toByteArray(inputStream);
	}

}
