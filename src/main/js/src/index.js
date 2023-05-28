import { RSocketClient, JsonSerializer, IdentitySerializer } from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';

// backend ws endpoint
const wsURL = 'ws://localhost:6565/rsocket';

// rsocket client
const client = new RSocketClient({
    serializers: {
        data: JsonSerializer,
        metadata: IdentitySerializer
    },
    setup: {
        keepAlive: 60000,
        lifetime: 180000,
        dataMimeType: 'application/json',
        metadataMimeType: 'message/x.rsocket.routing.v0',
    },
    transport: new RSocketWebSocketClient({
        url: wsURL
    })
});

// error handler
const errorHandler = (e) => console.log(e);
// response handler
const responseHandler = (payload) => {
    console.log('payload', payload)
    const li = document.createElement('li');
    li.innerText = payload.data.symbol;
    li.classList.add('list-group-item', 'small')
    document.getElementById('result').appendChild(li);
}

// request to rsocket-websocket and response handling
const tickerRequester = (socket, value) => {
    console.log('request', value)
    socket.requestStream({
                           data: value,
                           metadata: String.fromCharCode('ticker.stream'.length) + 'ticker.stream'
     }).subscribe({
        onError: errorHandler,
        onNext: responseHandler,
        onSubscribe: subscription => {
            subscription.request(6); // set it to some max value
        }
    });
}

// once the backend connection is established, register the event listeners
client.connect().then(socket => {
    document.getElementById('n').addEventListener('change', ({srcElement}) => {
        tickerRequester(socket, srcElement.value);
    })
}, errorHandler);