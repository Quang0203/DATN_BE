package datn.datnbe.Enum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum PayMentMethod {
    WALLET("My wallet"),
    CASH("Cash"),
    BANK_TRANSFER("Bank transfer"),
    ;
    private String name;

}
