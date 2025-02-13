package apt.auctionapi.controller.dto.response;

import apt.auctionapi.entity.Auction;
import apt.auctionapi.entity.auction.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public record AuctionResponse(
        String id,
        String keyword,
        LocalDateTime biddingDate,
        String pnu,
        String bjdCode,
        BjdInfoResponse bjdInfo,
        LocalDate registrationDate,
        LocalDate sellingDate,
        String category,
        String danjiId,
        Integer pyeong,
        Long appraisedPrice,
        Long lowestSellingPrice,
        Long sellingPrice,
        Integer numberOfFailures,
        Integer biddingDepositMin,
        Integer biddingDepositMax,
        Integer biddingDepositPercentMin,
        Integer biddingDepositPercentMax,
        String itemStatus,
        List<AuctionHistoryResponse> historyList,
        List<AuctionObjectResponse> objectList,
        List<OccupantInfoResponse> occupantInfoList,
        LandInfoResponse landInfo
) {
    public static List<AuctionResponse> from(List<Auction> auctions) {
        return auctions == null ? List.of() : auctions.stream()
                .map(AuctionResponse::of)
                .toList();
    }

    public static AuctionResponse from(Auction auction) {
        return AuctionResponse.of(auction);
    }

    private static AuctionResponse of(Auction auction) {
        return new AuctionResponse(
                auction.getId(),
                auction.getKeyword(),
                auction.getBiddingDate(),
                auction.getPnu(),
                auction.getBjdCode(),
                auction.getBjdInfo() != null ? BjdInfoResponse.from(auction.getBjdInfo()) : null,
                auction.getRegistrationDate(),
                auction.getSellingDate(),
                auction.getCategory(),
                auction.getDanjiId(),
                auction.getPyeong(),
                auction.getAppraisedPrice(),
                auction.getLowestSellingPrice(),
                auction.getSellingPrice(),
                auction.getNumberOfFailures(),
                auction.getBiddingDepositMin(),
                auction.getBiddingDepositMax(),
                auction.getBiddingDepositPercentMin(),
                auction.getBiddingDepositPercentMax(),
                auction.getItemStatus(),
                convertList(auction.getHistoryList(), AuctionHistoryResponse::from),
                convertList(auction.getObjectList(), AuctionObjectResponse::from),
                convertList(auction.getOccupantInfoList(), OccupantInfoResponse::from),
                auction.getLandInfo() != null ? LandInfoResponse.from(auction.getLandInfo()) : null
        );
    }

    // 리스트 변환 유틸리티 메서드
    private static <T, R> List<R> convertList(List<T> list, Function<T, R> mapper) {
        return list == null ? List.of() : list.stream().map(mapper).toList();
    }

    private record BjdInfoResponse(
            String sd,
            String sgg,
            String emd,
            String bjdCode,
            LocationResponse location
    ) {
        private static BjdInfoResponse from(BjdInfo bjdInfo) {
            return new BjdInfoResponse(
                    bjdInfo.getSd(),
                    bjdInfo.getSgg(),
                    bjdInfo.getEmd(),
                    bjdInfo.getBjdCode(),
                    bjdInfo.getLocation() != null ? LocationResponse.from(bjdInfo.getLocation()) : null
            );
        }
    }

    private record LocationResponse(
            Double x,
            Double y
    ) {
        private static LocationResponse from(Location location) {
            if (location == null) {
                return null;
            }
            return new LocationResponse(
                    location.getX(),
                    location.getY()
            );
        }
    }

    private record AuctionHistoryResponse(
            String auctionId,
            String caseId,
            String caseSite,
            Integer itemNumber,
            Integer historyOrder,
            String appointedDayType,
            LocalDateTime appointedDayAt,
            Integer numberOfFailures,
            Long lowestSellingPrice,
            String results
    ) {
        private static AuctionHistoryResponse from(AuctionHistory history) {
            return new AuctionHistoryResponse(
                    history.getAuctionId(),
                    history.getCaseId(),
                    history.getCaseSite(),
                    history.getItemNumber(),
                    history.getHistoryOrder(),
                    history.getAppointedDayType(),
                    history.getAppointedDayAt(),
                    history.getNumberOfFailures(),
                    history.getLowestSellingPrice(),
                    history.getResults()
            );
        }
    }

    private record AuctionObjectResponse(
            String auctionId,
            String caseId,
            String caseSite,
            Integer itemNumber,
            Integer objectNumber,
            String objectType,
            String usage,
            String objectAddress,
            Double groundTotalArea,
            Double buildingTotalArea
    ) {
        private static AuctionObjectResponse from(AuctionObject object) {
            return new AuctionObjectResponse(
                    object.getAuctionId(),
                    object.getCaseId(),
                    object.getCaseSite(),
                    object.getItemNumber(),
                    object.getObjectNumber(),
                    object.getObjectType(),
                    object.getUsage(),
                    object.getObjectAddress(),
                    object.getGroundTotalArea(),
                    object.getBuildingTotalArea()
            );
        }
    }

    private record OccupantInfoResponse(
            String occupant,
            LocalDateTime registrationDate,
            Integer isOpposingPower
    ) {
        private static OccupantInfoResponse from(OccupantInfo info) {
            return new OccupantInfoResponse(
                    info.getOccupant(),
                    info.getRegistrationDate(),
                    info.getIsOpposingPower()
            );
        }
    }

    private record LandInfoResponse(
            String pnu,
            String bjdCode,
            String landPurposeName1,
            String landUseName,
            Double area,
            List<UsagePlanResponse> usagePlanItems
    ) {
        private static LandInfoResponse from(LandInfo info) {
            return new LandInfoResponse(
                    info.getPnu(),
                    info.getBjdCode(),
                    info.getLandPurposeName(),
                    info.getLandUseName(),
                    info.getArea(),
                    convertList(info.getUsagePlanItems(), UsagePlanResponse::from)
            );
        }

        // 리스트 변환 유틸리티 메서드
        private static <T, R> List<R> convertList(List<T> list, Function<T, R> mapper) {
            return list == null ? List.of() : list.stream().map(mapper).toList();
        }
    }

    private record UsagePlanResponse(
            String landUsage,
            String conflictType
    ) {
        private static UsagePlanResponse from(UsagePlan plan) {
            return new UsagePlanResponse(
                    plan.getLandUsage(),
                    plan.getConflictType()
            );
        }
    }
}
