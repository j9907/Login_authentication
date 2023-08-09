import { PayloadAction, createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import axios from 'axios';

interface LoginForm {
    userid: string;
    password: string;
    remember:boolean;
}

interface User {
    username : string;
    userid : string;
}


export const LoginData = createAsyncThunk('auth/LoginData', async (param: User) => {
    try {
        console.log(param);
        const response = await axios.post('/login', param);
        return response.data as User;
    } catch (error: any) {
        throw error.response.data;
    }
});

interface LoginUsr {
    user: string | null;
    status: 'idle' | 'pending' | 'succeeded' | 'failed';
    error: string | null;
}

const initialState: LoginUsr = {
    user: null,
    status: 'idle',
    error: null,
};

const dataSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(LoginData.pending, (state) => {
                state.status = 'pending';
            })
            .addCase(LoginData.fulfilled, (state, action) => {
                state.status = 'succeeded';
                console.log(state.status)
                
            })
            .addCase(LoginData.rejected, (state, action: PayloadAction<any>) => { // 에러 정보를 할당하도록 수정
                state.status = 'failed';
                state.error = action.payload;
            });
    }
});

export default dataSlice.reducer;
