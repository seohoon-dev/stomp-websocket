<template>
  <v-container class="text-center">
    <v-row>
      <v-col>
        <h2>닉네임 입력</h2>
      </v-col>
    </v-row>

    <v-row v-if="!showRoomList">
      <v-col>
        <v-text-field solo v-model="nickname"></v-text-field>
      </v-col>
      <v-col lg="2">
        <v-btn @click="isShowRoomList">확인</v-btn>
      </v-col>
    </v-row>

    <div v-if="showRoomList">
      <v-row>
        <v-col>
          <p>환영합니다. {{ nickname }}님!</p>
        </v-col>
      </v-row>

      <h3>방 목록</h3>
      <div v-for="(room, index) in roomList" :key="index">
        <v-row class="text-left">
          <v-col>
            {{ room.name }}
          </v-col>
          <v-col>
            <v-btn @click="joinRoom(room.id)">join</v-btn>
          </v-col>
        </v-row>
      </div>

      <v-row>
        <v-col>
          <v-text-field solo v-model="sendMessage"></v-text-field>
        </v-col>
        <v-col lg="2">
          <v-btn @click="send(currRoomId)">Send</v-btn>
        </v-col>
      </v-row>

      <h3>채팅 내용</h3>
      <div v-for="(item, index) in recvMessage" :key="index">
        <v-row class="text-left">
          <v-col>
            {{ item.name }}: {{ item.content }}
          </v-col>
        </v-row>
      </div>
    </div>

    <v-btn @click="createRoom">TEST</v-btn>

  </v-container>
</template>

<script>
import Stomp from 'stomp-websocket'
import SockJS from 'sockjs-client'

let axios = require('axios')

export default {
  name: 'HelloWorld',

  data() {
    return {
      showRoomList: false,
      currRoomSubInfo: '',
      currRoomId: 1,
      roomList: [
        {"id": "1", "name": "즐챗", "count": 2},
        {"id": "2", "name": "소통", "count": 1},
        {"id": "3", "name": "롤 토크", "count": 3},
      ],
      nickname: "",
      sendMessage: "",
      recvMessage: [
        {"name": "길동", "content": "안녕!"},
        {"name": "수찬", "content": "배고파"},
        {"name": "민수", "content": "졸려"},
      ],
    }
  },
  created() {
    this.connect();
  },
  methods: {
    isShowRoomList() {
      this.showRoomList = true;
    },
    createRoom() {
      const roomCreationInfo = {
        creator: this.nickname,
        title: this.title,
      };

      axios.post(
          "/api/room",
          roomCreationInfo
      ).then(response => {
        console.log('테스트 메시지 입니다.', response.data);
        this.joinRoom(response.data); // 생성된 방에 참여
        this.currRoomId = response.data;
      }).catch(error => {
        console.log("에러 메시지 입니다.", error);
      });
    },
    joinRoom(roomId) {
      console.log(roomId + "번 방 참여");
      this.currRoomSubInfo = this.stompClient.subscribe("/topic/room/" + roomId, res => {
        console.log('채팅 메시지', res.body);
        this.recvMessage.push(JSON.parse(res.body));
      });

      this.stompClient.send("/app/join", {}, roomId);
    },
    exitRoom() {
      console.log("퇴장");
      this.currRoomSubInfo.unsubscribe();
    },
    send(currRoomId) {
      console.log("Send message : " + this.sendMessage);
      // if (this.stompClient && this.stompClient.connected) {
      const msg = {
        name: this.nickname,
        content: this.sendMessage
      };

      this.stompClient.send("/app/room/" + currRoomId, {}, JSON.stringify(msg));
      this.sendMessage = "";  // 입력창 초기화
    },
    connect() {
      const serverURL = "http://localhost:8080/demo-chatting"
      let socket = new SockJS(serverURL);
      this.stompClient = Stomp.over(socket);
      console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`)
      this.stompClient.connect({}, frame => {
            // 소켓 연결 성공
            this.connected = true;
            console.log('소켓 연결 성공', frame);

            // test sub
            this.testSub = this.stompClient.subscribe("/topic/test", res => {
              console.log('테스트 메시지 입니다.', res.body);
              this.recvMessage.push(res.body);
            });

            // this.stompClient.unsubscribe("/topic/test");

            this.stompClient.subscribe("/topic/update/room", res => {
              console.log('방 목록 업데이트', res.body);
              // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
              this.roomList.push(JSON.parse(res.body))
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
