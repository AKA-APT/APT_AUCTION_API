package apt.auctionapi.entity.auction;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class OccupantInfo {
    private String occupant;
    private LocalDateTime registrationDate;
    private Integer isOpposingPower;
}
