import {lazy, Suspense} from "react";
import Layout from "../layouts/Layout"; // Layout 컴포넌트 추가
const { createBrowserRouter } = require("react-router-dom");

const Loading = <div>Loading...</div>;

const Main = lazy(() => import("../pages/MainPage"));
const API_1 = lazy(() => import("../pages/API_1"));

const root = createBrowserRouter([
    {
        path: "/",
        element: <Layout />, // Layout을 최상위에 배치
        children: [
            {
                path: "",
                element: <Suspense fallback={Loading}><Main /></Suspense>
            },
            {
                path: "api/upbit/list",
                element: <Suspense fallback={Loading}><API_1 /></Suspense>
            },
            {
                path: "/api/upbit/pricesync",
                element: <Suspense fallback={Loading}><API_1 /></Suspense>
            }
        ]
    }
]);

export default root;
