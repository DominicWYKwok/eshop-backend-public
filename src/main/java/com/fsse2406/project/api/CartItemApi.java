package com.fsse2406.project.api;

import com.fsse2406.project.config.EnvConfig;
import com.fsse2406.project.data.cart.domainObject.CartItemResponseData;
import com.fsse2406.project.data.cart.dto.SuccessResponseDto;
import com.fsse2406.project.data.cart.dto.GetItemResponseDto;
import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.service.CartItemService;
import com.fsse2406.project.util.JwtUtil;
import jakarta.validation.constraints.Positive;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin({EnvConfig.DEV_BASE_URL, EnvConfig.PROD_BASE_URL})
//@CrossOrigin(origins = "http://localhost:5173")
public class CartItemApi {
    private final CartItemService cartItemService;

    public CartItemApi(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PutMapping("/{pid}/{quantity}")
    public SuccessResponseDto putCartItem(JwtAuthenticationToken jwt,
                                          @PathVariable String pid,
                                          @PathVariable Integer quantity) {
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwt);
//        CartItemResponseData cartItemResponseData = cartItemService.putCartItem(pid, quantity, firebaseUserData);
//        return new CartItemResponseDto(cartItemResponseData);
        cartItemService.putCartItem(pid, quantity, firebaseUserData);
        return new SuccessResponseDto();
    }

    @GetMapping
    public List<GetItemResponseDto> getCartItems(JwtAuthenticationToken jwt) {
        List<GetItemResponseDto> getAllItemResponseDtoList = new ArrayList<>();
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwt);
        for (CartItemResponseData cartItemResponseData : cartItemService.getAllCartItems(firebaseUserData)) {
            getAllItemResponseDtoList.add(new GetItemResponseDto(cartItemResponseData));
        }
        return getAllItemResponseDtoList;
    }

    @PatchMapping("/{pid}/{quantity}")
    public GetItemResponseDto updateCartItem(JwtAuthenticationToken jwt,
                                             @PathVariable String pid,
                                             @PathVariable @Positive Integer quantity) {
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwt);
        return new GetItemResponseDto(
                cartItemService.updateCartItem(pid, quantity, firebaseUserData)
        );
    }

    @DeleteMapping("/{pid}")
    public SuccessResponseDto deleteCartItem(JwtAuthenticationToken jwt, @PathVariable String pid) {
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwt);
        cartItemService.deleteCartItem(pid, firebaseUserData);
        return new SuccessResponseDto();
    }
}
