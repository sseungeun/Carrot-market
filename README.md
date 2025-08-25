# 당근마켓 클론 (Android)

간단한 지역 중고거래 클론 앱. 로그인/회원가입 → 게시글 목록 → 게시글 상세 → 채팅 → 업로드 플로우를 제공합니다.

## 기능 요약
- 로그인/회원가입: `ApiService.login()`(Retrofit) + `SharedPrefManager`로 세션 유지
- 메인(게시글 목록): RecyclerView + `PostAdapter`
- 게시글 상세: 상세 정보 표시, “채팅하기”로 `ChatActivity` 이동
- 채팅: Firebase Realtime Database 기반 실시간 송수신 (`ChatAdapter`, `Message`)
- 업로드: `UploadActivity`에서 게시글 생성, 위치 권한 승인 시 현재 위치 첨부

## 기술 스택
- Android (Java)
- Retrofit2 (REST 통신)
- Firebase Realtime Database (채팅)
- SharedPreferences (로컬 세션)
- RecyclerView/Adapter (UI)

## 프로젝트 구조(파일 참고)
[모앱프_Final Project.pdf](https://github.com/user-attachments/files/21963902/_Final.Project.pdf)
