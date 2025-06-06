package datn.datnbe.Controller;

import datn.datnbe.Service.BankTransferService;
import datn.datnbe.Service.RentACarService;
import datn.datnbe.Service.ReturnCarService;
import datn.datnbe.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/banktransfer")
public class BankTransferController {
    BankTransferService bankTransferService;
    RentACarService rentACarService;
    ReturnCarService returnCarService;

    @PostMapping("/createbanktransfer/{idbooking}")
    public ApiResponse paidDeposid(@PathVariable("idbooking") Integer idbooking) throws UnsupportedEncodingException {
        System.out.println(bankTransferService.paidDeposid(idbooking));
        return new ApiResponse()
                .builder()
                .result(bankTransferService.paidDeposid(idbooking))
                .build();
    }

    @GetMapping("/returnurl/paiddeposit")
    public ApiResponse returnUrlPaidDeposit(@RequestParam(value = "vnp_ResponseCode") String vnp_ResponseCode,
                                            @RequestParam(value = "vnp_OrderInfo") String vnp_OrderInfo) throws UnsupportedEncodingException {

        if(vnp_ResponseCode.equals("00")){
            return rentACarService.paidDeposid(Integer.parseInt(vnp_OrderInfo));
        }
        return new ApiResponse()
                .builder()
                .result(false)
                .build();
    }

    @GetMapping("/returnurl/completepayment")
    public ApiResponse returnUrlCompletePayment(@RequestParam(value = "vnp_ResponseCode") String vnp_ResponseCode,
                                 @RequestParam(value = "vnp_OrderInfo") String vnp_OrderInfo) throws UnsupportedEncodingException {

        if(vnp_ResponseCode.equals("00")){
            return returnCarService.returnCar(Integer.parseInt(vnp_OrderInfo));
        }
        return new ApiResponse()
                .builder()
                .result(false)
                .build();
    }
}
