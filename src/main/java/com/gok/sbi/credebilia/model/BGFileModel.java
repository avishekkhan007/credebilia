package com.gok.sbi.credebilia.model;

public class BGFileModel {



	private String guranteeNumber;
    private String amountOfGurantee;
    private String guranteeCoverFrom;
    private String lastDateOfLodgementOfClaim;
    private String nameOfFile;

    public String getGuranteeNumber() {
        return guranteeNumber;
    }

    public void setGuranteeNumber(String guranteeNumber) {
        this.guranteeNumber = guranteeNumber;
    }

    public String getAmountOfGurantee() {
        return amountOfGurantee;
    }

    public void setAmountOfGurantee(String amountOfGurantee) {
        this.amountOfGurantee = amountOfGurantee;
    }

    public String getGuranteeCoverFrom() {
        return guranteeCoverFrom;
    }

    public void setGuranteeCoverFrom(String guranteeCoverFrom) {
        this.guranteeCoverFrom = guranteeCoverFrom;
    }

    public String getLastDateOfLodgementOfClaim() {
        return lastDateOfLodgementOfClaim;
    }

    public void setLastDateOfLodgementOfClaim(String lastDateOfLodgementOfClaim) {
        this.lastDateOfLodgementOfClaim = lastDateOfLodgementOfClaim;
    }

    public String getNameOfFile() {
        return nameOfFile;
    }

    public void setNameOfFile(String nameOfFile) {
        this.nameOfFile = nameOfFile;
    }


    @Override
	public String toString() {
		return "BGFileModel [guranteeNumber=" + guranteeNumber + ", amountOfGurantee=" + amountOfGurantee
				+ ", guranteeCoverFrom=" + guranteeCoverFrom + ", lastDateOfLodgementOfClaim="
				+ lastDateOfLodgementOfClaim + ", nameOfFile=" + nameOfFile + "]";
	}
}
