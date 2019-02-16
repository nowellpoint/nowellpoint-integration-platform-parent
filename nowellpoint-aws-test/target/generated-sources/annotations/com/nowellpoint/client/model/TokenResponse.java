package com.nowellpoint.client.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Immutable implementation of {@link AbstractTokenResponse}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code TokenResponse.builder()}.
 */
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "AbstractTokenResponse"})
@Immutable
public final class TokenResponse extends AbstractTokenResponse {
  private final String accessToken;
  private final String tokenType;
  private final Long expiresIn;
  private final String scope;
  private final @Nullable String refreshToken;

  private TokenResponse(
      String accessToken,
      String tokenType,
      Long expiresIn,
      String scope,
      @Nullable String refreshToken) {
    this.accessToken = accessToken;
    this.tokenType = tokenType;
    this.expiresIn = expiresIn;
    this.scope = scope;
    this.refreshToken = refreshToken;
  }

  /**
   * @return The value of the {@code accessToken} attribute
   */
  @JsonProperty("access_token")
  @Override
  public String getAccessToken() {
    return accessToken;
  }

  /**
   * @return The value of the {@code tokenType} attribute
   */
  @JsonProperty("token_type")
  @Override
  public String getTokenType() {
    return tokenType;
  }

  /**
   * @return The value of the {@code expiresIn} attribute
   */
  @JsonProperty("expires_in")
  @Override
  public Long getExpiresIn() {
    return expiresIn;
  }

  /**
   * @return The value of the {@code scope} attribute
   */
  @JsonProperty("scope")
  @Override
  public String getScope() {
    return scope;
  }

  /**
   * @return The value of the {@code refreshToken} attribute
   */
  @JsonProperty("refresh_token")
  @Override
  public @Nullable String getRefreshToken() {
    return refreshToken;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenResponse#getAccessToken() accessToken} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for accessToken
   * @return A modified copy of the {@code this} object
   */
  public final TokenResponse withAccessToken(String value) {
    if (this.accessToken.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "accessToken");
    return new TokenResponse(newValue, this.tokenType, this.expiresIn, this.scope, this.refreshToken);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenResponse#getTokenType() tokenType} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for tokenType
   * @return A modified copy of the {@code this} object
   */
  public final TokenResponse withTokenType(String value) {
    if (this.tokenType.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "tokenType");
    return new TokenResponse(this.accessToken, newValue, this.expiresIn, this.scope, this.refreshToken);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenResponse#getExpiresIn() expiresIn} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for expiresIn
   * @return A modified copy of the {@code this} object
   */
  public final TokenResponse withExpiresIn(Long value) {
    if (this.expiresIn.equals(value)) return this;
    Long newValue = Preconditions.checkNotNull(value, "expiresIn");
    return new TokenResponse(this.accessToken, this.tokenType, newValue, this.scope, this.refreshToken);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenResponse#getScope() scope} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for scope
   * @return A modified copy of the {@code this} object
   */
  public final TokenResponse withScope(String value) {
    if (this.scope.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "scope");
    return new TokenResponse(this.accessToken, this.tokenType, this.expiresIn, newValue, this.refreshToken);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenResponse#getRefreshToken() refreshToken} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for refreshToken (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenResponse withRefreshToken(@Nullable String value) {
    if (Objects.equal(this.refreshToken, value)) return this;
    return new TokenResponse(this.accessToken, this.tokenType, this.expiresIn, this.scope, value);
  }

  /**
   * This instance is equal to all instances of {@code TokenResponse} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof TokenResponse
        && equalTo((TokenResponse) another);
  }

  private boolean equalTo(TokenResponse another) {
    return accessToken.equals(another.accessToken)
        && tokenType.equals(another.tokenType)
        && expiresIn.equals(another.expiresIn)
        && scope.equals(another.scope)
        && Objects.equal(refreshToken, another.refreshToken);
  }

  /**
   * Computes a hash code from attributes: {@code accessToken}, {@code tokenType}, {@code expiresIn}, {@code scope}, {@code refreshToken}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + accessToken.hashCode();
    h += (h << 5) + tokenType.hashCode();
    h += (h << 5) + expiresIn.hashCode();
    h += (h << 5) + scope.hashCode();
    h += (h << 5) + Objects.hashCode(refreshToken);
    return h;
  }

  /**
   * Prints the immutable value {@code TokenResponse} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("TokenResponse")
        .omitNullValues()
        .add("accessToken", accessToken)
        .add("tokenType", tokenType)
        .add("expiresIn", expiresIn)
        .add("scope", scope)
        .add("refreshToken", refreshToken)
        .toString();
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json extends AbstractTokenResponse {
    @Nullable String accessToken;
    @Nullable String tokenType;
    @Nullable Long expiresIn;
    @Nullable String scope;
    @Nullable String refreshToken;
    @JsonProperty("access_token")
    public void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
    }
    @JsonProperty("token_type")
    public void setTokenType(String tokenType) {
      this.tokenType = tokenType;
    }
    @JsonProperty("expires_in")
    public void setExpiresIn(Long expiresIn) {
      this.expiresIn = expiresIn;
    }
    @JsonProperty("scope")
    public void setScope(String scope) {
      this.scope = scope;
    }
    @JsonProperty("refresh_token")
    public void setRefreshToken(@Nullable String refreshToken) {
      this.refreshToken = refreshToken;
    }
    @Override
    public String getAccessToken() { throw new UnsupportedOperationException(); }
    @Override
    public String getTokenType() { throw new UnsupportedOperationException(); }
    @Override
    public Long getExpiresIn() { throw new UnsupportedOperationException(); }
    @Override
    public String getScope() { throw new UnsupportedOperationException(); }
    @Override
    public String getRefreshToken() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static TokenResponse fromJson(Json json) {
    TokenResponse.Builder builder = TokenResponse.builder();
    if (json.accessToken != null) {
      builder.accessToken(json.accessToken);
    }
    if (json.tokenType != null) {
      builder.tokenType(json.tokenType);
    }
    if (json.expiresIn != null) {
      builder.expiresIn(json.expiresIn);
    }
    if (json.scope != null) {
      builder.scope(json.scope);
    }
    if (json.refreshToken != null) {
      builder.refreshToken(json.refreshToken);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link AbstractTokenResponse} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable TokenResponse instance
   */
  public static TokenResponse copyOf(AbstractTokenResponse instance) {
    if (instance instanceof TokenResponse) {
      return (TokenResponse) instance;
    }
    return TokenResponse.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link TokenResponse TokenResponse}.
   * @return A new TokenResponse builder
   */
  public static TokenResponse.Builder builder() {
    return new TokenResponse.Builder();
  }

  /**
   * Builds instances of type {@link TokenResponse TokenResponse}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_ACCESS_TOKEN = 0x1L;
    private static final long INIT_BIT_TOKEN_TYPE = 0x2L;
    private static final long INIT_BIT_EXPIRES_IN = 0x4L;
    private static final long INIT_BIT_SCOPE = 0x8L;
    private long initBits = 0xfL;

    private @Nullable String accessToken;
    private @Nullable String tokenType;
    private @Nullable Long expiresIn;
    private @Nullable String scope;
    private @Nullable String refreshToken;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code AbstractTokenResponse} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(AbstractTokenResponse instance) {
      Preconditions.checkNotNull(instance, "instance");
      accessToken(instance.getAccessToken());
      tokenType(instance.getTokenType());
      expiresIn(instance.getExpiresIn());
      scope(instance.getScope());
      @Nullable String refreshTokenValue = instance.getRefreshToken();
      if (refreshTokenValue != null) {
        refreshToken(refreshTokenValue);
      }
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenResponse#getAccessToken() accessToken} attribute.
     * @param accessToken The value for accessToken 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("access_token")
    public final Builder accessToken(String accessToken) {
      this.accessToken = Preconditions.checkNotNull(accessToken, "accessToken");
      initBits &= ~INIT_BIT_ACCESS_TOKEN;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenResponse#getTokenType() tokenType} attribute.
     * @param tokenType The value for tokenType 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("token_type")
    public final Builder tokenType(String tokenType) {
      this.tokenType = Preconditions.checkNotNull(tokenType, "tokenType");
      initBits &= ~INIT_BIT_TOKEN_TYPE;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenResponse#getExpiresIn() expiresIn} attribute.
     * @param expiresIn The value for expiresIn 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("expires_in")
    public final Builder expiresIn(Long expiresIn) {
      this.expiresIn = Preconditions.checkNotNull(expiresIn, "expiresIn");
      initBits &= ~INIT_BIT_EXPIRES_IN;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenResponse#getScope() scope} attribute.
     * @param scope The value for scope 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("scope")
    public final Builder scope(String scope) {
      this.scope = Preconditions.checkNotNull(scope, "scope");
      initBits &= ~INIT_BIT_SCOPE;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenResponse#getRefreshToken() refreshToken} attribute.
     * @param refreshToken The value for refreshToken (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("refresh_token")
    public final Builder refreshToken(@Nullable String refreshToken) {
      this.refreshToken = refreshToken;
      return this;
    }

    /**
     * Builds a new {@link TokenResponse TokenResponse}.
     * @return An immutable instance of TokenResponse
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public TokenResponse build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new TokenResponse(accessToken, tokenType, expiresIn, scope, refreshToken);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_ACCESS_TOKEN) != 0) attributes.add("accessToken");
      if ((initBits & INIT_BIT_TOKEN_TYPE) != 0) attributes.add("tokenType");
      if ((initBits & INIT_BIT_EXPIRES_IN) != 0) attributes.add("expiresIn");
      if ((initBits & INIT_BIT_SCOPE) != 0) attributes.add("scope");
      return "Cannot build TokenResponse, some of required attributes are not set " + attributes;
    }
  }
}
