package com.fintech.paymentledger.controller;

import com.fintech.paymentledger.dto.TransferRequest;
import com.fintech.paymentledger.dto.TransferResult;
import com.fintech.paymentledger.service.TransferService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(
            TransferService transferService
    ) {
        this.transferService = transferService;
    }

    @PostMapping
    public TransferResult transfer(
            @RequestBody TransferRequest request
    ) {
        return transferService.transfer(request);
    }
}