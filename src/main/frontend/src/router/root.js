import {lazy, Suspense} from "react";
import Layout from "../layouts/Layout"; // Layout 컴포넌트 추가
const { createBrowserRouter } = require("react-router-dom");

const Loading = <div>Loading...</div>;

const Main = lazy(() => import("../pages/MainPage"));
const API_1 = lazy(() => import("../pages/upbit/UpbitMainPage"));
const AboutPage = lazy(() => import("../pages/AboutPage"));
const Contact = lazy(() => import("../pages/Contact"));

const root = createBrowserRouter([
    {
        path: "/",
        element: <Layout />,
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
                path: "/about",
                element: <Suspense fallback={Loading}><AboutPage /></Suspense>
            },
            {
                path: "/Contact",
                element: <Suspense fallback={Loading}><Contact /></Suspense>
            },
        ]
    }
]);

export default root;
