import axios from 'axios';

export const getMarketList = async () => {
    try {
        const res = await axios.get('/api/upbit/mklist');
        return res.data;
    } catch (error) {
        console.error('Failed to fetch Upbit Market list:', error);
        throw error;
    }
};

// Upbit 리스트를 가져오는 API
export const getPriceList = async () => {
    try {
        const res = await axios.get('/api/upbit/priceList');
        return res.data;
    } catch (error) {
        console.error('Failed to fetch Upbit list:', error);
        throw error;
    }
};


export const postUpbitFilterData = async (requestData) => {
    try {
        const res = await axios.post('/api/upbit/filterData', requestData);
        return res.data; // 서버로부터 받은 데이터 반환
    } catch (error) {
        console.error('Failed to fetch FilterData:', error);
        throw error;
    }
};

