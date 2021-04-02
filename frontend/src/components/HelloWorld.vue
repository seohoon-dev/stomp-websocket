<template>
  <v-container class="text-center">
    <div v-if="showInputName">
      <v-row>
        <v-col>
          <h2>닉네임 입력</h2>
        </v-col>
      </v-row>

      <v-row>
        <v-col>
          <v-text-field solo v-model="nickname"></v-text-field>
        </v-col>
        <v-col lg="2">
          <v-btn
              @click="[showInputName = false, showRoomList = true, getRoomList()]"
          >
            확인
          </v-btn>
        </v-col>
      </v-row>
    </div>

    <div v-if="!showInputName">
      <v-row>
        <v-col>
          <h3>환영합니다. {{ nickname }}님!</h3>
        </v-col>
      </v-row>
    </div>

    <div v-if="showRoomList">
      <v-row>
        <h3>방 목록</h3>
      </v-row>

      <v-row class="mt-10" v-for="(room, index) in roomList" :key="index">
        {{ room.title }}
        <v-spacer></v-spacer>
        <v-btn @click="joinRoom(room.id)">입장</v-btn>
      </v-row>
      <br>

      <v-row class="mt-5">
        <v-btn
            color="primary"
            dark
            @click.stop="dialog = true"
            width="100%"
        >
          방 만들기
        </v-btn>
      </v-row>

      <v-row justify="center">
        <v-dialog
            v-model="dialog"
            max-width="300"
        >
          <v-card>
            <v-card-title class="headline">
              방 제목
            </v-card-title>

            <v-text-field class="mx-4" v-model="roomTitle.title"></v-text-field>

            <v-card-actions>
              <v-spacer></v-spacer>

              <v-btn
                  color="primary darken-1"
                  text
                  @click="dialog = false"
              >
                취소
              </v-btn>

              <v-btn
                  color="primary darken-1"
                  text
                  @click="[dialog = false, createRoom()]"
              >
                확인
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-row>
    </div>

    <div v-if="showChatting">
      <v-row>
        <v-col>
          <h3 class="text-left">
            <v-icon class="exit" @click="exit">{{ 'mdi-arrow-left' }}</v-icon>
            채팅
          </h3>
        </v-col>
      </v-row>

      <v-row>
        <v-col>
          <v-text-field solo v-model="sendMessage" placeholder="채팅을 입력해주세요.."></v-text-field>
        </v-col>
        <v-col lg="2">
          <v-btn @click="send(currRoomId)">Send</v-btn>
        </v-col>
      </v-row>

      <v-row class="text-left" v-for="(item, index) in recvMessage" :key="index">
        <v-col>
          {{ item.name }}: {{ item.content }}
        </v-col>
      </v-row>
    </div>

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
      dialog: false,
      showInputName: true,
      showRoomList: false,
      showChatting: false,
      currRoomSubInfo: '',
      currRoomId: 1,
      roomList: [
        {"id": "1", "title": "즐챗", "count": 2},
        {"id": "2", "title": "소통", "count": 1},
        {"id": "3", "title": "롤 토크", "count": 3},
      ],
      nickname: '',
      roomTitle: {
        title: '',
      },
      sendMessage: '',
      recvMessage: [],
    }
  },
  created() {
    this.connect();
  },
  methods: {
    exit() {
      console.log('퇴장');
      this.showRoomList = true;
      this.showChatting = false;
      this.currRoomSubInfo.unsubscribe();
      this.recvMessage = [];
      this.stompClient.send("/app/exit", {}, this.currRoomId);
    },
    createRoom() {
      let my = this;
      console.log("roomTitle = " + my.roomTitle.title);

      axios.post(
          "/api/room",
          my.roomTitle
      ).then(response => {
        console.log('생성된 방 번호' + response.data);
        this.joinRoom(response.data); // 생성된 방에 참여
        this.currRoomId = response.data;
      }).catch(error => {
        console.log("에러 메시지 " + error);
      });
    },
    joinRoom(roomId) {
      console.log(roomId + "번 방 참여");
      this.showRoomList = false;
      this.showChatting = true;

      this.currRoomSubInfo = this.stompClient.subscribe("/topic/room/" + roomId, res => {
        console.log('채팅 메시지', res.body);
        this.recvMessage.push(JSON.parse(res.body));
      });

      this.stompClient.send("/app/join", {}, roomId);
      this.currRoomId = roomId;
    },
    getRoomList() {
      console.log("목록");
      this.stompClient.send("/app/list");
    },
    exitRoom() {
      console.log("퇴장");
      this.showRoomList = true;
      this.showChatting = false;

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

            this.stompClient.subscribe("/topic/list", res => {
              console.log('방 목록 업데이트', res.body);
              // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
              this.roomList = JSON.parse(res.body);
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
<style>
.exit:hover {
  background-color: skyblue;
  cursor: pointer;
}
</style>