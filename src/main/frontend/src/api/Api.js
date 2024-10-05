import axios from 'axios';

// Upbit 리스트를 가져오는 API
export const getUpbitList = async () => {
    try {
        const res = await axios.get('/api/upbit/list');
        return res.data;
    } catch (error) {
        console.error('Failed to fetch Upbit list:', error);
        throw error;
    }
};

// Upbit 데이터를 가져와서 동기화하는 API
export const fetchAndSync = async () => {
    try {
        const res = await axios.get('/api/upbit/fetchAndSync');
        return res.data;
    } catch (error) {
        console.error('Failed to fetch and sync data:', error);
        throw error;
    }
};

export const fetchPriceAndSync = async () => {
    try {
        const res = await axios.get('/api/upbit/pricesync');
        return res.data;
    } catch (error) {
        console.error('Failed to fetch and sync data:', error);
        throw error;
    }
};