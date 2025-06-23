import React, { useState } from 'react'
import { Button, Input } from 'antd'
import { useLoginMutation } from './api'

const LoginPage = () => {
  const [username, setUsername] = useState('')

  const [login] = useLoginMutation()

  const handleLogin = async () => {
    console.log('Login with:', username)
    // TODO: Gọi API login hoặc xử lý logic ở đây
    const response = await login(Number(username)).unwrap()

    localStorage.setItem('employeeId', String(response))
  }

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        background: '#f0f2f5',
      }}
    >
      <div
        style={{
          padding: '24px',
          background: '#fff',
          borderRadius: '8px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
          width: '300px',
        }}
      >
        <h2 style={{ textAlign: 'center', marginBottom: '16px' }}>Login</h2>
        <Input
          placeholder="Enter username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          style={{ marginBottom: '12px' }}
        />
        <Button type="primary" block onClick={handleLogin}>
          Login
        </Button>
      </div>
    </div>
  )
}

export default LoginPage
