package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.auth.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "🔐 Login", description = "사용자 로그인 관련 API")
public interface AuthApi {
    @Operation(summary = "로그인", description = "username과 password로 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 로그인되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "성공 예시",
                                    /*
                                    * 기존에는 공통 응답 객체를 반환하도록 구현되어 있었으나,
                                    * 코드잇에서 제공하는 정적 리소스를 연결하기 위해서는 해당 공통 응답 객체를 제거해야 했습니다.
                                    * 이에 따라 문서화 작업에서도 실제 응답 예시와 일치하도록, 공통 응답 객체 대신 실제 반환되는 값으로 수정하였습니다.
                                    * 이로 인해 성공 시에는 DTO가 직접 반환되고, 실패 시에는 공통 응답 객체로 감싸서 반환되는 차이가 존재합니다.
                                    * 추후 스프린트 미션이 진행되면서 이 부분은 보완될 예정입니다.
                                    * 현재 구현을 완전히 제거하지 않은 이유는, 이후 미션에서 해당 구조가 다시 필요해질 수 있다고 판단했기 때문입니다
                                    * (스프린트 미션 6에서 DTO와 매퍼가 추가된 것처럼요..!)
                                    * 성공과 실패 응답 형식을 통일하려고 했으나, 향후 추가 작업이 예정되어 있어 현재는 이와 같은 방식으로 구현되었음을 참고해주시면 감사하겠습니다.
                                    */
                                    value = """
                                                {
                                                    "id": "82170d94-e9e6-45df-874a-f4fb949f0835",
                                                    "username": "joy123",
                                                    "email": "joy123@inside.out",
                                                    "profileId": "e2189e67-cf5e-46b9-be45-d8299a1fc298",
                                                    "createdAt": "2025-07-14T05:25:07.135099Z",
                                                    "updatedAt": "2025-07-20T14:30:23.159294Z",
                                                    "online": true
                                                }
                                            """
                            )

                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name    = "BadRequest Example (INVALID)",
                                            summary = "필드 유효성 검사에 실패한 경우",
                                            value   = """
                                                {
                                                   "success": false,
                                                   "code": 400,
                                                   "message": "유효성 검사 실패",
                                                   "data": [
                                                       {
                                                           "field": "username",
                                                           "message": "공백일 수 없습니다"
                                                       },
                                                       {
                                                           "field": "password",
                                                           "message": "공백일 수 없습니다"
                                                       }
                                                   ],
                                                   "timestamp": "2025-07-20T23:53:22.782188"
                                               }
                                              """
                                    ),
                                    @ExampleObject(
                                            name    = "BadRequest Example (INVALID 2)",
                                            summary = "사용자가 존재하지 않은 경우",
                                            value   = """
                                                        {
                                                          "success": false,
                                                          "code": 400,
                                                          "message": "Bad Request Exception",
                                                          "data": "유저가 존재하지 않습니다: mingguriguri22",
                                                          "timestamp": "2025-07-09T18:20:46.61426"
                                                        }
                                                      """
                                    ),
                                    @ExampleObject(
                                            name    = "BadRequest Example (INVALID 3)",
                                            summary = "비밀번호가 일치하지 않은 경우",
                                            value   = """
                                                {
                                                      "success": false,
                                                      "code": 400,
                                                      "message": "Bad Request Exception",
                                                      "data": "비밀번호가 일치하지 않습니다.",
                                                      "timestamp": "2025-07-09T18:20:06.403191"
                                                }
                                              """
                                    )
                            }
                    ))
    })
    ResponseEntity<UserResponseDto> login(LoginRequestDto loginRequestDto);
}
