package com.shoppingcart.service;

import com.shoppingcart.dto.CartEditResponse;
import com.shoppingcart.entity.Cart;
import com.shoppingcart.entity.Product;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.repository.CartRepository;
import com.shoppingcart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public List<Product> getCartItems(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(()->new UnsupportedOperationException("Unauthorized"));
        var cart = cartRepository.findByUser(user);
        return cart.getProducts();
    }

    public CartEditResponse addToCart(Long productId, String userEmail) throws Exception {
        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("Unauthorized"));
        var cart = cartRepository.findByUser(user);
        Product product = productService.getProductById(productId);
        try {
            if (cart == null) {
                List<Product> cartProduct = new ArrayList<>();
                cartProduct.add(product);
                cart = Cart.builder()
                        .user(user)
                        .products(cartProduct)
                        .build();
            } else {
                cart.getProducts().add(product);
            }
            cartRepository.save(cart);
            return CartEditResponse.builder()
                    .message("Product successfully added to cart")
                    .build();
        } catch (Exception e) {
            throw new Exception("Failed to add product to cart");
        }
    }

    public CartEditResponse removeFromCart(Long productId, String userEmail) throws Exception {
        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("Unauthorized"));
        var cart = cartRepository.findByUser(user);
        CartEditResponse res = new CartEditResponse();

        if (cart.getProducts().isEmpty()) {
            res.setMessage("cart empty");
            return res;
        }

        var product = productService.getProductById(productId);
        var index = cart.getProducts().indexOf(product);

        if (index == -1) {
            res.setMessage("Failed to remove, product not found in cart");
            return res;
        }

        cart.getProducts().remove(index);
        cartRepository.save(cart);

        res.setMessage("Product removed from cart successfully");
        return res;
    }

    public boolean isProductExistsInCart(Long productId, String userEmail) throws UserNotFoundException {
        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("Unauthorized"));
        var cart = cartRepository.findByUser(user);
        var product = productService.getProductById(productId);
        return cart.getProducts().contains(product);
    }

    public Integer getCartCount(String userEmail) throws UserNotFoundException {
        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("Unauthorized"));
        var cart = cartRepository.findByUser(user);
        return cart.getProducts().size();
    }

    public boolean removeAllItemsFromCart(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Unauthorised"));
        var cart = cartRepository.findByUser(user);
        if(cart.getProducts().isEmpty()) {
           return false;
        }
        cart.getProducts().clear();
        cartRepository.save(cart);
        return true;
    }

    public Object checkout(String email) {
        var user = userRepository.findByEmail(email);
        return user;
    }
}
