package com.nexters.hytime.gitit.data.mapping

import com.nexters.hytime.gitit.data.dto.AccountResponse
import com.nexters.hytime.gitit.domain.model.Account

/**
 * [AccountResponse] DTO를 [Account] 도메인 모델로 변환한다.
 *
 * DTO가 UI나 도메인 계층으로 누출되지 않도록, 매핑은 이 확장 함수 하나로만 수행한다.
 */
internal fun AccountResponse.toDomain(): Account =
    Account(
        id = id,
        displayName = name,
        email = email,
        photoUrl = profileImageUrl,
    )
