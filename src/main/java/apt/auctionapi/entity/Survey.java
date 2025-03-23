package apt.auctionapi.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Document(collection = "auction_studies")
public class Survey {

    @Id
    private String id;

    @Field("dma_curstExmnMngInf")
    private ExamManagementInfo examManagementInfo;

    @Field("dlt_ordTsLserLtn")
    private List<OrderLeaserLocation> orderLeaserLocations;

    @Getter
    public static class ExamManagementInfo {
        @Field("cortOfcCd")
        private String courtOfficeCode;

        @Field("csNo")
        private String caseNumber;

        @Field("ordTsCnt")
        private Integer orderCount;

        @Field("auctnCurstExmnTrsmStatCd")
        private String auctionExamTransmissionStatusCode;

        @Field("execIntgCsNmCd")
        private String executionIntegratedCaseNameCode;

        @Field("lesDts")
        private String leaseDetails;

        @Field("exmndcRtrcnYmd")
        private String examDocumentRetractionDate;

        @Field("exmndcSndngYmd")
        private String examDocumentSendingDate;

        @Field("exmndcRcptnYmd")
        private String examDocumentReceptionDate;

        @Field("fstmLstPossRltnDts")
        private String firstTimePossessionRelationDetails;

        @Field("scntmLstPossRltnDts")
        private String secondTimePossessionRelationDetails;

        @Field("exmnDtDts")
        private String examinationDateDetails;

        @Field("lwstDvsCd")
        private String lawsuitDivisionCode;

        @Field("lstPossRltnDts")
        private String possessionRelationDetails;

        @Field("userCsNo")
        private String userCaseNumber;

        @Field("printRltnDts")
        private String printRelationDetails;
    }

    @Getter
    public static class OrderLeaserLocation {
        @Field("cortOfcCd")
        private String courtOfficeCode;

        @Field("csNo")
        private String caseNumber;

        @Field("ordTsCnt")
        private Integer orderCount;

        @Field("intrpsSeq")
        private Integer interpositionSequence;

        @Field("objctSeq")
        private Integer objectSequence;

        @Field("btprtPrsnlDvsCd")
        private String bothPartyPersonalDivisionCode;

        @Field("enrrno")
        private String enrollmentNumber;

        @Field("zpcd")
        private String zipCode;

        @Field("basAddr")
        private String baseAddress;

        @Field("objctDtlAddr")
        private String objectDetailAddress;

        @Field("auctnLesUsgCd")
        private String auctionLeaseUsageCode;

        @Field("lesUsgDts")
        private String leaseUsageDetails;

        @Field("lesDposDts")
        private String leaseDisposalDetails;

        @Field("mmrntAmtDts")
        private String monthlyRentAmountDetails;

        @Field("gdsPossCtt")
        private String goodsPossessionContent;

        @Field("mvinDtlCtt")
        private String movingDetailContent;

        @Field("rgstryCrtcpCfmtnCtt")
        private String registryCertificationConfirmationContent;

        @Field("lesPartCtt")
        private String leasePartContent;

        @Field("lesDtsRmk")
        private String leaseDetailsRemark;

        @Field("adongSdNm")
        private String addressSidoName;

        @Field("adongSggNm")
        private String addressSigunguName;

        @Field("adongEmdNm")
        private String addressEupmyeondongName;

        @Field("adongRiNm")
        private String addressRiName;

        @Field("rprsLtnoAddr")
        private String representativeLotNumberAddress;

        @Field("rdnmSdNm")
        private String roadNameSidoName;

        @Field("rdnmSggNm")
        private String roadNameSigunguName;

        @Field("rdnmEmdNm")
        private String roadNameEupmyeondongName;

        @Field("rdnm")
        private String roadName;

        @Field("rdnmBldNo")
        private String roadNameBuildingNumber;

        @Field("rdnmRefcAddr")
        private String roadNameReferenceAddress;

        @Field("addrTypCd")
        private String addressTypeCode;

        @Field("bldNm")
        private String buildingName;

        @Field("auctnLstDvsCd")
        private String auctionListDivisionCode;

        @Field("auctnIntrpsDvsCd")
        private String auctionInterpositionDivisionCode;

        @Field("intrpsNm")
        private String interpositionName;

        @Field("bldDtlDts")
        private String buildingDetailDetails;

        @Field("printSt")
        private String printStatement;
    }
}