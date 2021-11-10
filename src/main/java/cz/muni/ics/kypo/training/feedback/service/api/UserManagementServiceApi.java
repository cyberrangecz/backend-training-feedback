package cz.muni.ics.kypo.training.feedback.service.api;

import cz.muni.ics.kypo.training.feedback.dto.resolver.PageResultResource;
import cz.muni.ics.kypo.training.feedback.dto.resolver.UserRefDTO;
import cz.muni.ics.kypo.training.feedback.exceptions.CustomWebClientException;
import cz.muni.ics.kypo.training.feedback.exceptions.MicroserviceApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserManagementServiceApi {

    private final WebClient userManagementServiceWebClient;

    /**
     * Gets users with given user ref ids.
     *
     * @param userRefIds the user ref ids
     * @param pageable   pageable parameter with information about pagination.
     * @param givenName  optional parameter used for filtration
     * @param familyName optional parameter used for filtration
     * @return the users with given user ref ids
     */
    public PageResultResource<UserRefDTO> getUsersByIds(Set<Long> userRefIds, Pageable pageable, String givenName, String familyName) {
        if (userRefIds.isEmpty()) {
            return new PageResultResource<>(Collections.emptyList(), new PageResultResource.Pagination(0, 0, pageable.getPageSize(), 0, 0));
        }
        try {
            return userManagementServiceWebClient
                    .get()
                    .uri(uriBuilder -> {
                                uriBuilder
                                        .path("/users/ids")
                                        .queryParam("ids", StringUtils.collectionToDelimitedString(userRefIds, ","));
                                this.setCommonParams(givenName, familyName, pageable, uriBuilder);
                                return uriBuilder.build();
                            }
                    )
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<PageResultResource<UserRefDTO>>() {
                    })
                    .block();
        } catch (CustomWebClientException ex) {
            throw new MicroserviceApiException("Error when calling user management service API to obtain users by IDs: " + userRefIds + ".", ex);
        }
    }

    private void setCommonParams(String givenName, String familyName, Pageable pageable, UriBuilder builder) {
        if (givenName != null) {
            builder.queryParam("givenName", givenName);
        }
        if (familyName != null) {
            builder.queryParam("familyName", familyName);
        }
        builder.queryParam("page", pageable.getPageNumber());
        builder.queryParam("size", Math.min(pageable.getPageSize(), 999));
    }
}
