<template>
  <v-container class="text-center">
    <v-row>
      <v-col>
        <h2>Contents</h2>
      </v-col>
    </v-row>

    <v-row>
      <v-col>
        <v-btn>Connect</v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Stomp from 'stomp-websocket'
import SockJS from 'sockjs-client'

export default {
  name: 'HelloWorld',

  data() {
    return {};
  },
  created() {
    this.connect();
  },
  methods: {
    connect() {
      const serverURL = "http://localhost:8080/demo-chatting"
      let socket = new SockJS(serverURL);
      this.stompClient = Stomp.over(socket);
      console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`)
      this.stompClient.connect(
          {},
          frame => {
            // 소켓 연결 성공
            this.connected = true;
            console.log('소켓 연결 성공', frame);
            // 서버의 메시지 전송 endpoint를 구독합니다.
            // 이런형태를 pub sub 구조라고 합니다.
            this.stompClient.subscribe("/send", res => {
              console.log('구독으로 받은 메시지 입니다.', res.body);
              // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
              // this.recvList.push(JSON.parse(res.body))
            });
          },
          error => {
            // 소켓 연결 실패
            console.log('소켓 연결 실패', error);
            this.connected = false;
          }
      );
    }
  }
}
</script>
