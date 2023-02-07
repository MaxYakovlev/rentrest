package ru.yakovlev.rentrest.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yakovlev.rentrest.model.dto.rent.RentDto;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.entity.Rent;
import ru.yakovlev.rentrest.model.mapping.RentMapper;
import ru.yakovlev.rentrest.security.SecurityContext;
import ru.yakovlev.rentrest.service.rent.RentService;
import ru.yakovlev.rentrest.service.user.UserService;

import javax.ws.rs.core.UriBuilder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/rents")
@RequiredArgsConstructor
public class RentController {
    private final RentService rentService;
    private final RentMapper rentMapper;
    private final UserService userService;

    @Value("${telegram.bot.token}")
    private String botToken;

    @GetMapping
    public List<RentDto> findAll(){
        List<Rent> rents = rentService.findAll();
        return rentMapper.modelToDto(rents);
    }

    @SneakyThrows
    @PatchMapping("/close/{id}")
    public HttpStatus close(@PathVariable Long id){
        AppUser appUser = userService.findById(SecurityContext.get().getId());
        Rent closedRent = rentService.closeRent(appUser, id, 47.238195429359116, 39.71261707106315);

        Long minutes = ChronoUnit.MINUTES.between(closedRent.getStartRentDatetime(), closedRent.getEndRentDatetime());
        if(minutes == 0){
            minutes = 1L;
        }

        String message = "Аренда закрыта, списано " + closedRent.getAmount() + ".00 RUB за " + minutes + " мин.";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("https://api.telegram.org")
                .path("/{token}/sendMessage")
                .queryParam("chat_id", appUser.getChatId())
                .queryParam("text", message);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(builder.build("bot" + botToken))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        return HttpStatus.OK;
    }
}
