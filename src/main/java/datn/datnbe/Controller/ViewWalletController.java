package datn.datnbe.Controller;

import datn.datnbe.Service.ViewWalletService;
import datn.datnbe.dto.request.TopUpRequest;
import datn.datnbe.dto.request.ViewWalletRequest;
import datn.datnbe.dto.request.WithdrawRequest;
import datn.datnbe.dto.response.ViewWalletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;

@RestController
@RequestMapping("/viewWallet")
public class ViewWalletController {

    @Autowired
    private ViewWalletService viewWalletService;

//    @GetMapping("/{userId}")
//    public ViewWalletResponse viewWallet(@PathVariable int userId) {
//        ViewWalletRequest request = new ViewWalletRequest();
//        request.setUserId(userId);
//        return viewWalletService.viewWallet(request);
//    }

    @GetMapping
    public ViewWalletResponse viewWallet() {
        return viewWalletService.viewWallet();
    }

    @PostMapping("/topup")
    public ViewWalletResponse topUpWallet(@RequestBody TopUpRequest topUpRequest) {
        return viewWalletService.topUpWallet(topUpRequest);
    }

    @PostMapping("/withdraw")
    public ViewWalletResponse withdrawFromWallet(@RequestBody WithdrawRequest withdrawRequest) {
        return viewWalletService.withdrawFromWallet(withdrawRequest);
    }

    @GetMapping("/search")
    public ViewWalletResponse searchTransactions(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return viewWalletService.searchTransactions(start, end);
    }

}

