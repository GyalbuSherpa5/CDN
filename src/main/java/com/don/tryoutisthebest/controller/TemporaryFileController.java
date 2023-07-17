package com.don.tryoutisthebest.controller;

import com.don.tryoutisthebest.model.TemporaryFile;
import com.don.tryoutisthebest.resources.CheckerResponseDto;
import com.don.tryoutisthebest.resources.MakerResponseDto;
import com.don.tryoutisthebest.resources.UploadRequestDto;
import com.don.tryoutisthebest.service.TemporaryFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("files")
public class TemporaryFileController {

    private final TemporaryFileService fileService;

    @PostMapping("/request")
    public Mono<String> uploadTemporaryFile(FilePart filePart, @RequestPart UploadRequestDto file) throws IOException {
        return fileService.saveTemporaryInfo(filePart,file);
    }

    @GetMapping("/getRequest/{userName}")
    public Flux<CheckerResponseDto> getAllRequestedFileForChecker(@PathVariable String userName) {
        return fileService.getAllRequests(userName);
    }
    @GetMapping("/getMyRequest/{userName}")
    public Flux<MakerResponseDto> getMyRequests(@PathVariable String userName){
        return fileService.getMyRequest(userName);
    }

    @PostMapping("/approve/{approvedByWhoUser}/{fileName}/{yesKiNo}")
    public Mono<String> approve(@PathVariable String approvedByWhoUser, @PathVariable String fileName, @PathVariable boolean yesKiNo) {

        fileService.giveApproval(approvedByWhoUser, fileName, yesKiNo);

        return Mono.just("approve vayo dai");
    }

    @DeleteMapping("/deleteAllTemp")
    public Mono<Void> deleteAll() {
        return fileService.deleteAllFileTemp();
    }


}
