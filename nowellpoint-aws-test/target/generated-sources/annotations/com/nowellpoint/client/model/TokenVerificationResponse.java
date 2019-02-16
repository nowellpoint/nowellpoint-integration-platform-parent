package com.nowellpoint.client.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Immutable implementation of {@link AbstractTokenVerificationResponse}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code TokenVerificationResponse.builder()}.
 */
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "AbstractTokenVerificationResponse"})
@Immutable
public final class TokenVerificationResponse
    extends AbstractTokenVerificationResponse {
  private final Boolean active;
  private final @Nullable String scope;
  private final @Nullable String username;
  private final @Nullable Long expirationTime;
  private final @Nullable Long issuedAt;
  private final @Nullable String subject;
  private final @Nullable String audience;
  private final @Nullable String issuer;
  private final @Nullable String id;
  private final @Nullable String tokenType;
  private final @Nullable String clientId;
  private final @Nullable String userId;
  private final List<String> groups;

  private TokenVerificationResponse(TokenVerificationResponse.Builder builder) {
    this.active = builder.active;
    this.scope = builder.scope;
    this.username = builder.username;
    this.expirationTime = builder.expirationTime;
    this.issuedAt = builder.issuedAt;
    this.subject = builder.subject;
    this.audience = builder.audience;
    this.issuer = builder.issuer;
    this.id = builder.id;
    this.tokenType = builder.tokenType;
    this.clientId = builder.clientId;
    this.userId = builder.userId;
    this.groups = builder.groupsIsSet()
        ? createUnmodifiableList(true, builder.groups)
        : createUnmodifiableList(false, createSafeList(super.getGroups(), true, false));
  }

  private TokenVerificationResponse(
      Boolean active,
      @Nullable String scope,
      @Nullable String username,
      @Nullable Long expirationTime,
      @Nullable Long issuedAt,
      @Nullable String subject,
      @Nullable String audience,
      @Nullable String issuer,
      @Nullable String id,
      @Nullable String tokenType,
      @Nullable String clientId,
      @Nullable String userId,
      List<String> groups) {
    this.active = active;
    this.scope = scope;
    this.username = username;
    this.expirationTime = expirationTime;
    this.issuedAt = issuedAt;
    this.subject = subject;
    this.audience = audience;
    this.issuer = issuer;
    this.id = id;
    this.tokenType = tokenType;
    this.clientId = clientId;
    this.userId = userId;
    this.groups = groups;
  }

  /**
   * @return The value of the {@code active} attribute
   */
  @JsonProperty("active")
  @Override
  public Boolean getActive() {
    return active;
  }

  /**
   * @return The value of the {@code scope} attribute
   */
  @JsonProperty("scope")
  @Override
  public @Nullable String getScope() {
    return scope;
  }

  /**
   * @return The value of the {@code username} attribute
   */
  @JsonProperty("username")
  @Override
  public @Nullable String getUsername() {
    return username;
  }

  /**
   * @return The value of the {@code expirationTime} attribute
   */
  @JsonProperty("exp")
  @Override
  public @Nullable Long getExpirationTime() {
    return expirationTime;
  }

  /**
   * @return The value of the {@code issuedAt} attribute
   */
  @JsonProperty("iat")
  @Override
  public @Nullable Long getIssuedAt() {
    return issuedAt;
  }

  /**
   * @return The value of the {@code subject} attribute
   */
  @JsonProperty("sub")
  @Override
  public @Nullable String getSubject() {
    return subject;
  }

  /**
   * @return The value of the {@code audience} attribute
   */
  @JsonProperty("aud")
  @Override
  public @Nullable String getAudience() {
    return audience;
  }

  /**
   * @return The value of the {@code issuer} attribute
   */
  @JsonProperty("iss")
  @Override
  public @Nullable String getIssuer() {
    return issuer;
  }

  /**
   * @return The value of the {@code id} attribute
   */
  @JsonProperty("jti")
  @Override
  public @Nullable String getId() {
    return id;
  }

  /**
   * @return The value of the {@code tokenType} attribute
   */
  @JsonProperty("token_type")
  @Override
  public @Nullable String getTokenType() {
    return tokenType;
  }

  /**
   * @return The value of the {@code clientId} attribute
   */
  @JsonProperty("client_id")
  @Override
  public @Nullable String getClientId() {
    return clientId;
  }

  /**
   * @return The value of the {@code userId} attribute
   */
  @JsonProperty("uid")
  @Override
  public @Nullable String getUserId() {
    return userId;
  }

  /**
   * @return The value of the {@code groups} attribute
   */
  @JsonProperty("groups")
  @Override
  public List<String> getGroups() {
    return groups;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getActive() active} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for active
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withActive(Boolean value) {
    if (this.active.equals(value)) return this;
    Boolean newValue = Objects.requireNonNull(value, "active");
    return new TokenVerificationResponse(
        newValue,
        this.scope,
        this.username,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        this.audience,
        this.issuer,
        this.id,
        this.tokenType,
        this.clientId,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getScope() scope} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for scope (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withScope(@Nullable String value) {
    if (Objects.equals(this.scope, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        value,
        this.username,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        this.audience,
        this.issuer,
        this.id,
        this.tokenType,
        this.clientId,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getUsername() username} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for username (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withUsername(@Nullable String value) {
    if (Objects.equals(this.username, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        value,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        this.audience,
        this.issuer,
        this.id,
        this.tokenType,
        this.clientId,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getExpirationTime() expirationTime} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for expirationTime (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withExpirationTime(@Nullable Long value) {
    if (Objects.equals(this.expirationTime, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        value,
        this.issuedAt,
        this.subject,
        this.audience,
        this.issuer,
        this.id,
        this.tokenType,
        this.clientId,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getIssuedAt() issuedAt} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for issuedAt (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withIssuedAt(@Nullable Long value) {
    if (Objects.equals(this.issuedAt, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        this.expirationTime,
        value,
        this.subject,
        this.audience,
        this.issuer,
        this.id,
        this.tokenType,
        this.clientId,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getSubject() subject} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for subject (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withSubject(@Nullable String value) {
    if (Objects.equals(this.subject, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        this.expirationTime,
        this.issuedAt,
        value,
        this.audience,
        this.issuer,
        this.id,
        this.tokenType,
        this.clientId,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getAudience() audience} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for audience (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withAudience(@Nullable String value) {
    if (Objects.equals(this.audience, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        value,
        this.issuer,
        this.id,
        this.tokenType,
        this.clientId,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getIssuer() issuer} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for issuer (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withIssuer(@Nullable String value) {
    if (Objects.equals(this.issuer, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        this.audience,
        value,
        this.id,
        this.tokenType,
        this.clientId,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getId() id} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for id (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withId(@Nullable String value) {
    if (Objects.equals(this.id, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        this.audience,
        this.issuer,
        value,
        this.tokenType,
        this.clientId,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getTokenType() tokenType} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for tokenType (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withTokenType(@Nullable String value) {
    if (Objects.equals(this.tokenType, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        this.audience,
        this.issuer,
        this.id,
        value,
        this.clientId,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getClientId() clientId} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for clientId (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withClientId(@Nullable String value) {
    if (Objects.equals(this.clientId, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        this.audience,
        this.issuer,
        this.id,
        this.tokenType,
        value,
        this.userId,
        this.groups);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractTokenVerificationResponse#getUserId() userId} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for userId (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final TokenVerificationResponse withUserId(@Nullable String value) {
    if (Objects.equals(this.userId, value)) return this;
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        this.audience,
        this.issuer,
        this.id,
        this.tokenType,
        this.clientId,
        value,
        this.groups);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link AbstractTokenVerificationResponse#getGroups() groups}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final TokenVerificationResponse withGroups(String... elements) {
    List<String> newValue = createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        this.audience,
        this.issuer,
        this.id,
        this.tokenType,
        this.clientId,
        this.userId,
        newValue);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link AbstractTokenVerificationResponse#getGroups() groups}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of groups elements to set
   * @return A modified copy of {@code this} object
   */
  public final TokenVerificationResponse withGroups(Iterable<String> elements) {
    if (this.groups == elements) return this;
    List<String> newValue = createUnmodifiableList(false, createSafeList(elements, true, false));
    return new TokenVerificationResponse(
        this.active,
        this.scope,
        this.username,
        this.expirationTime,
        this.issuedAt,
        this.subject,
        this.audience,
        this.issuer,
        this.id,
        this.tokenType,
        this.clientId,
        this.userId,
        newValue);
  }

  /**
   * This instance is equal to all instances of {@code TokenVerificationResponse} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof TokenVerificationResponse
        && equalTo((TokenVerificationResponse) another);
  }

  private boolean equalTo(TokenVerificationResponse another) {
    return active.equals(another.active)
        && Objects.equals(scope, another.scope)
        && Objects.equals(username, another.username)
        && Objects.equals(expirationTime, another.expirationTime)
        && Objects.equals(issuedAt, another.issuedAt)
        && Objects.equals(subject, another.subject)
        && Objects.equals(audience, another.audience)
        && Objects.equals(issuer, another.issuer)
        && Objects.equals(id, another.id)
        && Objects.equals(tokenType, another.tokenType)
        && Objects.equals(clientId, another.clientId)
        && Objects.equals(userId, another.userId)
        && groups.equals(another.groups);
  }

  /**
   * Computes a hash code from attributes: {@code active}, {@code scope}, {@code username}, {@code expirationTime}, {@code issuedAt}, {@code subject}, {@code audience}, {@code issuer}, {@code id}, {@code tokenType}, {@code clientId}, {@code userId}, {@code groups}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + active.hashCode();
    h += (h << 5) + Objects.hashCode(scope);
    h += (h << 5) + Objects.hashCode(username);
    h += (h << 5) + Objects.hashCode(expirationTime);
    h += (h << 5) + Objects.hashCode(issuedAt);
    h += (h << 5) + Objects.hashCode(subject);
    h += (h << 5) + Objects.hashCode(audience);
    h += (h << 5) + Objects.hashCode(issuer);
    h += (h << 5) + Objects.hashCode(id);
    h += (h << 5) + Objects.hashCode(tokenType);
    h += (h << 5) + Objects.hashCode(clientId);
    h += (h << 5) + Objects.hashCode(userId);
    h += (h << 5) + groups.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code TokenVerificationResponse} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "TokenVerificationResponse{"
        + "active=" + active
        + ", scope=" + scope
        + ", username=" + username
        + ", expirationTime=" + expirationTime
        + ", issuedAt=" + issuedAt
        + ", subject=" + subject
        + ", audience=" + audience
        + ", issuer=" + issuer
        + ", id=" + id
        + ", tokenType=" + tokenType
        + ", clientId=" + clientId
        + ", userId=" + userId
        + ", groups=" + groups
        + "}";
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json extends AbstractTokenVerificationResponse {
    @Nullable Boolean active;
    @Nullable String scope;
    @Nullable String username;
    @Nullable Long expirationTime;
    @Nullable Long issuedAt;
    @Nullable String subject;
    @Nullable String audience;
    @Nullable String issuer;
    @Nullable String id;
    @Nullable String tokenType;
    @Nullable String clientId;
    @Nullable String userId;
    List<String> groups = Collections.emptyList();
    boolean groupsIsSet;
    @JsonProperty("active")
    public void setActive(Boolean active) {
      this.active = active;
    }
    @JsonProperty("scope")
    public void setScope(@Nullable String scope) {
      this.scope = scope;
    }
    @JsonProperty("username")
    public void setUsername(@Nullable String username) {
      this.username = username;
    }
    @JsonProperty("exp")
    public void setExpirationTime(@Nullable Long expirationTime) {
      this.expirationTime = expirationTime;
    }
    @JsonProperty("iat")
    public void setIssuedAt(@Nullable Long issuedAt) {
      this.issuedAt = issuedAt;
    }
    @JsonProperty("sub")
    public void setSubject(@Nullable String subject) {
      this.subject = subject;
    }
    @JsonProperty("aud")
    public void setAudience(@Nullable String audience) {
      this.audience = audience;
    }
    @JsonProperty("iss")
    public void setIssuer(@Nullable String issuer) {
      this.issuer = issuer;
    }
    @JsonProperty("jti")
    public void setId(@Nullable String id) {
      this.id = id;
    }
    @JsonProperty("token_type")
    public void setTokenType(@Nullable String tokenType) {
      this.tokenType = tokenType;
    }
    @JsonProperty("client_id")
    public void setClientId(@Nullable String clientId) {
      this.clientId = clientId;
    }
    @JsonProperty("uid")
    public void setUserId(@Nullable String userId) {
      this.userId = userId;
    }
    @JsonProperty("groups")
    public void setGroups(List<String> groups) {
      this.groups = groups;
      this.groupsIsSet = true;
    }
    @Override
    public Boolean getActive() { throw new UnsupportedOperationException(); }
    @Override
    public String getScope() { throw new UnsupportedOperationException(); }
    @Override
    public String getUsername() { throw new UnsupportedOperationException(); }
    @Override
    public Long getExpirationTime() { throw new UnsupportedOperationException(); }
    @Override
    public Long getIssuedAt() { throw new UnsupportedOperationException(); }
    @Override
    public String getSubject() { throw new UnsupportedOperationException(); }
    @Override
    public String getAudience() { throw new UnsupportedOperationException(); }
    @Override
    public String getIssuer() { throw new UnsupportedOperationException(); }
    @Override
    public String getId() { throw new UnsupportedOperationException(); }
    @Override
    public String getTokenType() { throw new UnsupportedOperationException(); }
    @Override
    public String getClientId() { throw new UnsupportedOperationException(); }
    @Override
    public String getUserId() { throw new UnsupportedOperationException(); }
    @Override
    public List<String> getGroups() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static TokenVerificationResponse fromJson(Json json) {
    TokenVerificationResponse.Builder builder = TokenVerificationResponse.builder();
    if (json.active != null) {
      builder.active(json.active);
    }
    if (json.scope != null) {
      builder.scope(json.scope);
    }
    if (json.username != null) {
      builder.username(json.username);
    }
    if (json.expirationTime != null) {
      builder.expirationTime(json.expirationTime);
    }
    if (json.issuedAt != null) {
      builder.issuedAt(json.issuedAt);
    }
    if (json.subject != null) {
      builder.subject(json.subject);
    }
    if (json.audience != null) {
      builder.audience(json.audience);
    }
    if (json.issuer != null) {
      builder.issuer(json.issuer);
    }
    if (json.id != null) {
      builder.id(json.id);
    }
    if (json.tokenType != null) {
      builder.tokenType(json.tokenType);
    }
    if (json.clientId != null) {
      builder.clientId(json.clientId);
    }
    if (json.userId != null) {
      builder.userId(json.userId);
    }
    if (json.groupsIsSet) {
      builder.groups(json.groups);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link AbstractTokenVerificationResponse} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable TokenVerificationResponse instance
   */
  public static TokenVerificationResponse copyOf(AbstractTokenVerificationResponse instance) {
    if (instance instanceof TokenVerificationResponse) {
      return (TokenVerificationResponse) instance;
    }
    return TokenVerificationResponse.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link TokenVerificationResponse TokenVerificationResponse}.
   * @return A new TokenVerificationResponse builder
   */
  public static TokenVerificationResponse.Builder builder() {
    return new TokenVerificationResponse.Builder();
  }

  /**
   * Builds instances of type {@link TokenVerificationResponse TokenVerificationResponse}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_ACTIVE = 0x1L;
    private static final long OPT_BIT_GROUPS = 0x1L;
    private long initBits = 0x1L;
    private long optBits;

    private @Nullable Boolean active;
    private @Nullable String scope;
    private @Nullable String username;
    private @Nullable Long expirationTime;
    private @Nullable Long issuedAt;
    private @Nullable String subject;
    private @Nullable String audience;
    private @Nullable String issuer;
    private @Nullable String id;
    private @Nullable String tokenType;
    private @Nullable String clientId;
    private @Nullable String userId;
    private List<String> groups = new ArrayList<String>();

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code AbstractTokenVerificationResponse} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(AbstractTokenVerificationResponse instance) {
      Objects.requireNonNull(instance, "instance");
      active(instance.getActive());
      @Nullable String scopeValue = instance.getScope();
      if (scopeValue != null) {
        scope(scopeValue);
      }
      @Nullable String usernameValue = instance.getUsername();
      if (usernameValue != null) {
        username(usernameValue);
      }
      @Nullable Long expirationTimeValue = instance.getExpirationTime();
      if (expirationTimeValue != null) {
        expirationTime(expirationTimeValue);
      }
      @Nullable Long issuedAtValue = instance.getIssuedAt();
      if (issuedAtValue != null) {
        issuedAt(issuedAtValue);
      }
      @Nullable String subjectValue = instance.getSubject();
      if (subjectValue != null) {
        subject(subjectValue);
      }
      @Nullable String audienceValue = instance.getAudience();
      if (audienceValue != null) {
        audience(audienceValue);
      }
      @Nullable String issuerValue = instance.getIssuer();
      if (issuerValue != null) {
        issuer(issuerValue);
      }
      @Nullable String idValue = instance.getId();
      if (idValue != null) {
        id(idValue);
      }
      @Nullable String tokenTypeValue = instance.getTokenType();
      if (tokenTypeValue != null) {
        tokenType(tokenTypeValue);
      }
      @Nullable String clientIdValue = instance.getClientId();
      if (clientIdValue != null) {
        clientId(clientIdValue);
      }
      @Nullable String userIdValue = instance.getUserId();
      if (userIdValue != null) {
        userId(userIdValue);
      }
      addAllGroups(instance.getGroups());
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getActive() active} attribute.
     * @param active The value for active 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("active")
    public final Builder active(Boolean active) {
      this.active = Objects.requireNonNull(active, "active");
      initBits &= ~INIT_BIT_ACTIVE;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getScope() scope} attribute.
     * @param scope The value for scope (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("scope")
    public final Builder scope(@Nullable String scope) {
      this.scope = scope;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getUsername() username} attribute.
     * @param username The value for username (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("username")
    public final Builder username(@Nullable String username) {
      this.username = username;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getExpirationTime() expirationTime} attribute.
     * @param expirationTime The value for expirationTime (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("exp")
    public final Builder expirationTime(@Nullable Long expirationTime) {
      this.expirationTime = expirationTime;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getIssuedAt() issuedAt} attribute.
     * @param issuedAt The value for issuedAt (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("iat")
    public final Builder issuedAt(@Nullable Long issuedAt) {
      this.issuedAt = issuedAt;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getSubject() subject} attribute.
     * @param subject The value for subject (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("sub")
    public final Builder subject(@Nullable String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getAudience() audience} attribute.
     * @param audience The value for audience (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("aud")
    public final Builder audience(@Nullable String audience) {
      this.audience = audience;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getIssuer() issuer} attribute.
     * @param issuer The value for issuer (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("iss")
    public final Builder issuer(@Nullable String issuer) {
      this.issuer = issuer;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getId() id} attribute.
     * @param id The value for id (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("jti")
    public final Builder id(@Nullable String id) {
      this.id = id;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getTokenType() tokenType} attribute.
     * @param tokenType The value for tokenType (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("token_type")
    public final Builder tokenType(@Nullable String tokenType) {
      this.tokenType = tokenType;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getClientId() clientId} attribute.
     * @param clientId The value for clientId (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("client_id")
    public final Builder clientId(@Nullable String clientId) {
      this.clientId = clientId;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractTokenVerificationResponse#getUserId() userId} attribute.
     * @param userId The value for userId (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("uid")
    public final Builder userId(@Nullable String userId) {
      this.userId = userId;
      return this;
    }

    /**
     * Adds one element to {@link AbstractTokenVerificationResponse#getGroups() groups} list.
     * @param element A groups element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addGroups(String element) {
      this.groups.add(Objects.requireNonNull(element, "groups element"));
      optBits |= OPT_BIT_GROUPS;
      return this;
    }

    /**
     * Adds elements to {@link AbstractTokenVerificationResponse#getGroups() groups} list.
     * @param elements An array of groups elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addGroups(String... elements) {
      for (String element : elements) {
        this.groups.add(Objects.requireNonNull(element, "groups element"));
      }
      optBits |= OPT_BIT_GROUPS;
      return this;
    }

    /**
     * Sets or replaces all elements for {@link AbstractTokenVerificationResponse#getGroups() groups} list.
     * @param elements An iterable of groups elements
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("groups")
    public final Builder groups(Iterable<String> elements) {
      this.groups.clear();
      return addAllGroups(elements);
    }

    /**
     * Adds elements to {@link AbstractTokenVerificationResponse#getGroups() groups} list.
     * @param elements An iterable of groups elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllGroups(Iterable<String> elements) {
      for (String element : elements) {
        this.groups.add(Objects.requireNonNull(element, "groups element"));
      }
      optBits |= OPT_BIT_GROUPS;
      return this;
    }

    /**
     * Builds a new {@link TokenVerificationResponse TokenVerificationResponse}.
     * @return An immutable instance of TokenVerificationResponse
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public TokenVerificationResponse build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new TokenVerificationResponse(this);
    }

    private boolean groupsIsSet() {
      return (optBits & OPT_BIT_GROUPS) != 0;
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<String>();
      if ((initBits & INIT_BIT_ACTIVE) != 0) attributes.add("active");
      return "Cannot build TokenVerificationResponse, some of required attributes are not set " + attributes;
    }
  }

  private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
    ArrayList<T> list;
    if (iterable instanceof Collection<?>) {
      int size = ((Collection<?>) iterable).size();
      if (size == 0) return Collections.emptyList();
      list = new ArrayList<T>();
    } else {
      list = new ArrayList<T>();
    }
    for (T element : iterable) {
      if (skipNulls && element == null) continue;
      if (checkNulls) Objects.requireNonNull(element, "element");
      list.add(element);
    }
    return list;
  }

  private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
    switch(list.size()) {
    case 0: return Collections.emptyList();
    case 1: return Collections.singletonList(list.get(0));
    default:
      if (clone) {
        return Collections.unmodifiableList(new ArrayList<T>(list));
      } else {
        if (list instanceof ArrayList<?>) {
          ((ArrayList<?>) list).trimToSize();
        }
        return Collections.unmodifiableList(list);
      }
    }
  }
}
