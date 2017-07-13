/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  TextInput,
  NativeModules,
  DeviceEventEmitter,
  ToastAndroid,
} from 'react-native';
var phoneCallModule = NativeModules.testname;

export default class NativeConnectDemo extends Component {
  constructor(props){
    super(props);
    this.state= {
      text:'',
      title:'拨号验证'
    }
  }

  componentWillMount() {
       DeviceEventEmitter.addListener('showToast', function  (msg) {
          this.setState({
            title:'验证通过'
          });
          console.log(msg);
          ToastAndroid.show("DeviceEventEmitter收到消息:" + "\n" + msg.key, ToastAndroid.SHORT)
       }.bind(this));
   }

   showToast(){
     console.log("11111");
   }


  send(){
    phoneCallModule.startToCall(this.state.text);
  }

  render() {
    return (
      <View style={styles.container}>
        <View style={{flexDirection:'row',justifyContent:'center',alignItems:'center',height:100,width:400}}>
          <TextInput
          style={{height: 40,width:150, borderColor: 'gray', borderWidth: 1}}
          onChangeText={(text) => this.setState({text})}
          value={this.state.text}
          />
          <TouchableOpacity onPress={this.send.bind(this)}>
            <Text>{this.state.title}</Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('NativeConnectDemo', () => NativeConnectDemo);
