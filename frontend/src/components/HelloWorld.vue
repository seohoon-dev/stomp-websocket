<template>
  <v-container>
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
      <v-row class="mb-5">
        <v-col class="text-center">
          <h3>
            환영합니다. {{ nickname }}님!
            <v-menu
                open-on-hover
                down
                offset-y
            >
              <template v-slot:activator="{ on, attrs }">
                <v-icon
                    v-if="noti"
                    color="primary"
                    v-bind="attrs"
                    v-on="on"
                    @click="noti = false"
                >
                  {{ 'mdi-bell-ring' }}
                </v-icon>
                <v-icon
                    v-else
                    color="primary"
                    v-bind="attrs"
                    v-on="on"
                >
                  {{ 'mdi-bell' }}
                </v-icon>
              </template>

              <v-list>
                <v-list-item
                    v-for="(item, index) in notiList"
                    :key="index"
                >
                  <v-list-item-title>{{ item }}</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
          </h3>
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

      <v-row class="mt-5">
        <v-btn
            color="gray"
            dark
            width="100%"
            @click="sendNoti"
        >
          전체 공지
        </v-btn>
      </v-row>
    </div>

    <div v-if="showChatting">
      <v-row>
        <v-col>
          <h3 class="text-left">
            <v-icon class="exit" @click="exitRoom">{{ 'mdi-arrow-left' }}</v-icon>
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
      noti: false,
      notiList: ['first noti', 'second noti'],
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
      retry: 0,
    }
  },
  created() {
    this.connect();
    this.connectSSE();
  },
  methods: {
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
    exitRoom() {
      console.log('퇴장');
      this.showRoomList = true;
      this.showChatting = false;
      this.currRoomSubInfo.unsubscribe();
      this.recvMessage = [];
      this.stompClient.send("/app/exit", {}, this.currRoomId);
    },
    getRoomList() {
      console.log("목록");
      this.stompClient.send("/app/list");
    },
    send(currRoomId) {
      console.log("Send message : " + this.sendMessage);

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
            console.log('소켓 연결 성공', frame);

            this.stompClient.subscribe("/topic/list", res => {
              console.log('방 목록 업데이트', res.body);
              // 받은 데이터를 json 으로 파싱하고 리스트에 넣어줍니다.
              this.roomList = JSON.parse(res.body);
            });
          },
          error => {
            console.log('STOMP connection failed: ' + error);
            if (this.retry < 3) { // 연결 재시도
              console.log('STOMP: Reconnecting in 10 seconds');
              setTimeout(this.connect, 10000);
              this.retry++;
            } else {  // 연결 실패
              this.retry = 0;
            }
          }
      );
    },
    connectSSE() {
      console.log("sse connect...")
      const eventSource = new EventSource(`/api/sub/noti?id=${Math.random()}`);

      eventSource.onopen = res => console.log(res);

      eventSource.onerror = res => console.log(res);

      eventSource.onmessage = res => {
        console.log("sse response = " + res.data);
        if (res.data !== null) {
          this.noti = true;
          this.notiList.push(res.data);
        }
      };
    },
    sendNoti() {
      axios.get(
          "/api/pub/noti"
      ).catch(error => {
        console.log("에러 메시지 " + error);
      });
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