import React, { useState } from 'react';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Checkbox, Form, Input } from 'antd';
import './css/Logincom.css'
import {useDispatch} from 'react-redux';
import {LoginData} from '../modules/Loginutil'
import { fetchUserData } from '../modules/typetest';
import {auth} from '../modules/store'


interface User {
  username : string;
  userid : string;
}

const App: React.FC = () => {

    const[User , setUser] = useState({
        userid : '',
        password : '',
    });
    const dispatch = useDispatch<auth>();


const onFinish = (values : User) => {
  dispatch(LoginData(values))
}

  const onChange = (e : React.ChangeEvent<HTMLInputElement>) => {
    const {name, value} = e.target;
    setUser({
        ...User,
        [name] : value
    });

}

  console.log(User)
  return (
    <div className="container">
        <h2>Login</h2>
    <Form
      name="normal_login"
      className="login-form"
      initialValues={{ remember: true }}
     onFinish={onFinish}
        
    >
      <Form.Item
        name="userid"
        rules={[{ required: true, message: 'Please input your Userid!' }]}
        
      >
        <Input prefix={<UserOutlined className="site-form-item-icon" />} placeholder="userid" 
        value={User.userid} onChange={onChange} name="userid"
        />
      </Form.Item>
      <Form.Item
        name="password"
        rules={[{ required: true, message: 'Please input your Password!' }]}
      >
        <Input
          prefix={<LockOutlined className="site-form-item-icon" />}
          type="password"
          name="password"
          placeholder="Password"
          value={User.password}
          onChange={onChange}
        />
      </Form.Item>
      <Form.Item>
        <Form.Item name="remember" valuePropName="checked" noStyle>
          <Checkbox>Remember me</Checkbox>
        </Form.Item>

      
      </Form.Item>

      <Form.Item>
        <Button type="primary" htmlType="submit" className="login-form-button">
          Log in
        </Button>
      </Form.Item>
    </Form>
    </div>
  );
};

export default App;