package com.fsse2406.project.api;

import com.fsse2406.project.config.EnvConfig;
import com.fsse2406.project.data.transaction.domainObject.TransactionResponseData;
import com.fsse2406.project.data.transaction.dto.PaySuccessfullyResponseDto;
import com.fsse2406.project.data.transaction.dto.TransactionResponseDto;
import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.service.TransactionService;
import com.fsse2406.project.util.JwtUtil;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
@CrossOrigin({EnvConfig.DEV_BASE_URL, EnvConfig.PROD_BASE_URL})
public class TransactionApi {
    private final TransactionService transactionService;

    public TransactionApi(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public TransactionResponseDto prepareTransaction(JwtAuthenticationToken jwt) {
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwt);
        TransactionResponseData transactionResponseData = transactionService.prepareTransaction(firebaseUserData);
        return new TransactionResponseDto(transactionResponseData);
    }

    @GetMapping("/{tid}")
    public TransactionResponseDto getTransactionByTid(JwtAuthenticationToken jwt,
                                                      @PathVariable String tid) {
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwt);
        TransactionResponseData transactionResponseData = transactionService.getTransactionById(firebaseUserData, tid);
        return new TransactionResponseDto(transactionResponseData);
    }

    @PatchMapping({"/{tid}/pay"})
    public PaySuccessfullyResponseDto payTransaction(JwtAuthenticationToken jwt,
                                                     @PathVariable String tid){
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwt);
        return new PaySuccessfullyResponseDto(transactionService.payTransaction(firebaseUserData, tid));
    }

    @PatchMapping({"/{tid}/finish"})
    public TransactionResponseDto finishTransaction(JwtAuthenticationToken jwt,
                                                     @PathVariable String tid){
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwt);
        TransactionResponseData transactionResponseData = transactionService.finishTransaction(firebaseUserData, tid);
        return new TransactionResponseDto(transactionResponseData);
    }
}