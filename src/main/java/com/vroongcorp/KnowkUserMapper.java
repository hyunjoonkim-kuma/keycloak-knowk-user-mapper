package com.vroongcorp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;
import org.yaml.snakeyaml.Yaml;

public class KnowkUserMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper,
    OIDCIDTokenMapper, UserInfoTokenMapper {


  private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();
  public static final String PROVIDER_ID = "oidc-knowk-user-mapper";

  static {
    // The builtin protocol mapper let the user define under which claim name (key)
    // the protocol mapper writes its value. To display this option in the generic dialog
    // in keycloak, execute the following method.
    OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
    // The builtin protocol mapper let the user define for which tokens the protocol mapper
    // is executed (access token, id token, user info). To add the config options for the different types
    // to the dialog execute the following method. Note that the following method uses the interfaces
    // this token mapper implements to decide which options to add to the config. So if this token
    // mapper should never be available for some sort of options, e.g. like the id token, just don't
    // implement the corresponding interface.
    OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, KnowkUserMapper.class);
  }

  @Override
  public String getDisplayCategory() {
    return "Token mapper";
  }

  @Override
  public String getDisplayType() {
    return "Hello World Mapper";
  }

  @Override
  public String getHelpText() {
    return "Adds a hello world text to the claim";
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return configProperties;
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

  @Override
  protected void setClaim(final IDToken token,
      final ProtocolMapperModel mappingModel,
      final UserSessionModel userSession,
      final KeycloakSession keycloakSession,
      final ClientSessionContext clientSessionCtx) {

    // 환경별로 구분된 YAML 설정 파일에서 REST API URL을 로드
    //String environment = System.getProperty("keycloak.env", "dev"); // 예: -Dkeycloak.env=prod
    String restApiUrl = loadRestApiUrl("local");


    String currentRealmName = keycloakSession.getContext().getRealm().getName();

    // todo 통신하는 모듈 작업 필요
    // knowk User 도메인의 user_id 값
    System.out.println("restApiUrl = " + restApiUrl);
    System.out.println(
        "keycloak custom mapper test. getUser userId = " + userSession.getUser().getId());

    System.out.println("currentRealmName = " + currentRealmName);
    OIDCAttributeMapperHelper.mapClaim(token, mappingModel, "hello world");
  }

  public static String loadRestApiUrl(String environment) {
    Yaml yaml = new Yaml();
    try (InputStream in = KnowkUserMapper.class
        .getClassLoader()
        .getResourceAsStream("config-" + environment + ".yaml")) {
      Map<String, Object> data = yaml.load(in);
      return data.get("restApiUrl") != null ? (String) data.get("restApiUrl") : "local";
    } catch (Exception e) {
      return "local";
    }
  }
}
