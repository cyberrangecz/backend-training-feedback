package cz.cyberrange.platform.training.feedback.dto.resolver;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * Encapsulates information about user reference.
 */
@ApiModel(
        value = "UserRefDTO",
        description = "User information from user-and-group microservice is mapped to this class " +
                "and is also used to provide information about authors, participants, and organizers."
)
public class UserRefDTO {

    @ApiModelProperty(value = "Reference to user in another microservice and get his id", example = "1")
    private Long userRefId;
    @ApiModelProperty(value = "Reference to user in another microservice.", example = "john.doe@mail.com")
    @JsonProperty("sub")
    private String userRefSub;
    @ApiModelProperty(value = "Reference to user in another microservice and get his full name", example = "Mgr. John Doe")
    @JsonProperty("full_name")
    private String userRefFullName;
    @ApiModelProperty(value = "User given name", example = "John")
    @JsonProperty("given_name")
    private String userRefGivenName;
    @ApiModelProperty(value = "User family name", example = "Doe")
    @JsonProperty("family_name")
    private String userRefFamilyName;
    @ApiModelProperty(value = "Reference to user in another microservice and get his iss", example = "https://oidc.provider.cz")
    private String iss;
    @ApiModelProperty(value = "Identicon of a user.", example = "iVBORw0KGgoAAAANSUhEUgAAAEsAAABLCAYAAAA4TnrqAAACIUlEQVR4Xu3YsY0dSQxAQQUlpXT5Z3CS/YgxSrQa4gLlEOBb9pj/x6//fv7/t/78/XhN3yBWyz3kBX2DWC33kBf0DWK13ENe0DeI1XIPeUHfIFbLPeQFfYNYLfeQF/QNYrXcQ17QN4jVcg95Qd8gVss95AV9g1gt95AX9A1itdxDXtA3iNVyD3lB3yBWyz3kBX2DWC33kBf0DWLERGOiLdGWaEuMgeghoi3RlmhLjIHoIaIt0ZZoS4yB6CGiLdGWaEuMgeghoi3RlmhLjIHoIaIt0ZZoS4yB6CGiLdGWaEuMgeghoi3RlmhLjIHoIaIt0ZZoS4yB6CGiLdGWaEuMgeghoi3RlmhLjIHoIaIt0ZZoS4yB6CGiLdGWaEuMgeghoi3RlmhLjIHoIaIt0ZZoS6z+8b/mPha4jwXuY4H7WOA+FriPBe5jgftY4D4WuI8F7mOB+1jgPha4jwXGbzbn2xicb2Nwvo3B+TYG59sYnG9jcL6Nwfk2BufbGJxvY3C+jcH5Ngbn2xicb2Nwvq1+z2pMtCXaEm2J1XIPEW2JtkRbYrXcQ0Rboi3Rllgt9xDRlmhLtCVWyz1EtCXaEm2J1XIPEW2JtkRbYrXcQ0Rboi3Rllgt9xDRlmhLtCVWyz1EtCXaEm2J1XIPEW2JtkRbYrXcQ0Rboi3Rllgt9xDRlmhLtCVWyz1EtCXaEm2J1XIPEW2JtkRbYrXcQ0Rboi3RlvgNt34wfeJElG8AAAAASUVORK5CYII=")
    private byte[] picture;

    /**
     * Gets user ref sub.
     *
     * @return the user ref sub
     */
    public String getUserRefSub() {
        return userRefSub;
    }

    /**
     * Sets user ref sub.
     *
     * @param userRefSub the user ref sub
     */
    public void setUserRefSub(String userRefSub) {
        this.userRefSub = userRefSub;
    }

    /**
     * Gets iss.
     *
     * @return the iss
     */
    public String getIss() {
        return iss;
    }

    /**
     * Sets iss.
     *
     * @param iss the iss
     */
    public void setIss(String iss) {
        this.iss = iss;
    }

    /**
     * Gets user ref id.
     *
     * @return the user ref id
     */
    @JsonProperty("user_ref_id")
    public Long getUserRefId() {
        return userRefId;
    }

    /**
     * Sets user ref id.
     *
     * @param userRefId the user ref id
     */
    @JsonAlias({"id", "user_ref_id"})
    public void setUserRefId(Long userRefId) {
        this.userRefId = userRefId;
    }

    /**
     * Gets user ref full name.
     *
     * @return the user ref full name
     */
    public String getUserRefFullName() {
        return userRefFullName;
    }

    /**
     * Sets user ref full name.
     *
     * @param userRefFullName the user ref full name
     */
    public void setUserRefFullName(String userRefFullName) {
        this.userRefFullName = userRefFullName;
    }

    /**
     * Gets user ref given name.
     *
     * @return the user ref given name
     */
    public String getUserRefGivenName() {
        return userRefGivenName;
    }

    /**
     * Sets user ref given name.
     *
     * @param userRefGivenName the user ref given name
     */
    public void setUserRefGivenName(String userRefGivenName) {
        this.userRefGivenName = userRefGivenName;
    }

    /**
     * Gets user ref family name.
     *
     * @return the user ref family name
     */
    public String getUserRefFamilyName() {
        return userRefFamilyName;
    }

    /**
     * Sets user ref family name.
     *
     * @param userRefFamilyName the user ref family name
     */
    public void setUserRefFamilyName(String userRefFamilyName) {
        this.userRefFamilyName = userRefFamilyName;
    }

    /**
     * Gets the identicon of the user encoded in base64.
     *
     * @return identicon of the user.
     */
    public byte[] getPicture() {
        return picture;
    }

    /**
     * Sets the identicon of the user encoded in base64.
     *
     * @param picture encoded identicon of the user.
     */
    public void setPicture(byte[] picture) {
        this.picture = picture;
    }


    @Override
    public String toString() {
        return "UserRefDTO{" +
                ", userRefSub='" + userRefSub + '\'' +
                ", userRefFullName='" + userRefFullName + '\'' +
                ", userRefGivenName='" + userRefGivenName + '\'' +
                ", userRefFamilyName='" + userRefFamilyName + '\'' +
                ", iss='" + iss + '\'' +
                ", userRefId=" + userRefId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRefDTO)) return false;
        UserRefDTO that = (UserRefDTO) o;
        return Objects.equals(getUserRefId(), that.getUserRefId()) &&
                Objects.equals(getUserRefSub(), that.getUserRefSub()) &&
                Objects.equals(getUserRefFullName(), that.getUserRefFullName()) &&
                Objects.equals(getUserRefGivenName(), that.getUserRefGivenName()) &&
                Objects.equals(getUserRefFamilyName(), that.getUserRefFamilyName()) &&
                Objects.equals(getIss(), that.getIss());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserRefId(), getUserRefSub(), getUserRefFullName(), getUserRefGivenName(), getUserRefFamilyName(), getIss());
    }
}

