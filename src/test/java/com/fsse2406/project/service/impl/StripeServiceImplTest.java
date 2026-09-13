package com.fsse2406.project.service.impl;

import com.fsse2406.project.config.EnvConfig;
import com.fsse2406.project.repository.ProductRepository;
import com.fsse2406.project.repository.StripeRepository;
import com.fsse2406.project.service.ProductService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class StripeServiceImplTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(ProductService.class, () -> mock(ProductService.class))
            .withBean(StripeRepository.class, () -> mock(StripeRepository.class))
            .withBean(ProductRepository.class, () -> mock(ProductRepository.class))
            .withBean(StripeServiceImpl.class)
            .withPropertyValues("stripe.secret.key=sk_test_placeholder");

    @Test
    void productionSessionUsesProductionReturnUrls() {
        assertReturnUrls("prod", EnvConfig.PROD_BASE_URL);
    }

    @Test
    void developmentSessionUsesLocalReturnUrls() {
        assertReturnUrls("development", EnvConfig.DEV_BASE_URL);
    }

    private void assertReturnUrls(String profile, String expectedDomain) {
        contextRunner.withPropertyValues("spring.profiles.active=" + profile).run(context -> {
            assertThat(context).hasNotFailed();
            try (var sessions = mockStatic(Session.class)) {
                Session session = new Session();
                session.setId("cs_test_placeholder");
                session.setUrl("https://checkout.stripe.com/test");
                sessions.when(() -> Session.create(any(SessionCreateParams.class)))
                        .thenAnswer(invocation -> {
                            SessionCreateParams params = invocation.getArgument(0);
                            assertThat(params.getSuccessUrl()).isEqualTo(expectedDomain + "/payment-success/t-1");
                            assertThat(params.getCancelUrl()).isEqualTo(expectedDomain + "/error");
                            return session;
                        });

                context.getBean(StripeServiceImpl.class).createStripeSession(List.of(), "t-1");

                sessions.verify(() -> Session.create(any(SessionCreateParams.class)));
            }
        });
    }
}
