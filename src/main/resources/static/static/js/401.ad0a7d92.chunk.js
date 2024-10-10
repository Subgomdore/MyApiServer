"use strict";(self.webpackChunkfrontend=self.webpackChunkfrontend||[]).push([[401],{401:(e,t,s)=>{s.r(t),s.d(t,{default:()=>n});var r=s(43),a=s(213);var i=s(579);const n=function(){const[e,t]=(0,r.useState)(""),[s,n]=(0,r.useState)("all"),[c,l]=(0,r.useState)({krwList:[],btcList:[],usdtList:[]}),[o,d]=(0,r.useState)(0);(0,r.useEffect)((()=>{(async()=>{try{return(await a.A.get("/api/upbit/priceList")).data}catch(e){throw console.error("Failed to fetch Upbit list:",e),e}})().then((t=>{m(t,e,s)}))}),[e,s]),(0,r.useEffect)((()=>{const e=window.location.protocol+"//"+window.location.host,t=new EventSource(e+"/api/sse/price");return t.addEventListener("price",(e=>{let t=JSON.parse(e.data);"KRW-BTC"===t.code&&d(t.trade_price),l((e=>{const s=e.krwList.map((e=>{if(e.market===t.code){const s=t.trade_price;return{...e,price:h(s)}}return e})),r=e.btcList.map((e=>{if(e.market===t.code){const s=t.trade_price;return{...e,price:h(s)}}return e}));return{...e,krwList:s,btcList:r}}))})),()=>{console.log("Closing SSE connection"),t.close()}}),[]);const h=e=>{let t=Number(e);if(t>1)return Math.floor(t).toString();if(t<1){return t.toFixed(10).replace(/\.?0+$/,"")}return isNaN(t)?"Price not available":t.toString()},m=(e,t,s)=>{let r=[],a=[],i=[];const n=e.COIN,c=e.PRICE;for(let l in n){const e=n[l].market;for(let t in c)c[t].market===e&&(n[l].price=h(c[t].trade_price),n[l].prev_closing_price=h(c[t].prev_closing_price),n[l].priceChangeClass=p(c[t].trade_price,c[t].prev_closing_price));e.startsWith("KRW-")&&n[l].korean_name.toLowerCase().includes(t)&&("all"===s||"krw"===s)?r.push(n[l]):e.startsWith("BTC-")&&n[l].korean_name.toLowerCase().includes(t)&&("all"===s||"btc"===s)?a.push(n[l]):!n[l].korean_name.toLowerCase().includes(t)||"all"!==s&&"usdt"!==s||i.push(n[l])}console.log(r),l({krwList:r,btcList:a,usdtList:i})},p=(e,t)=>e>t?"price-up":e<t?"price-down":"price-stable";return(0,i.jsxs)("div",{children:[(0,i.jsxs)("div",{className:"search-and-buttons",children:[(0,i.jsxs)("div",{children:[(0,i.jsx)("button",{onClick:()=>alert("Button 1 Clicked!"),children:"Button 1"}),(0,i.jsx)("button",{onClick:()=>alert("Button 2 Clicked!"),children:"Button 2"}),(0,i.jsx)("button",{onClick:()=>alert("Button 3 Clicked!"),children:"Button 3"})]}),(0,i.jsxs)("div",{style:{display:"flex",alignItems:"center"},children:[(0,i.jsx)("input",{type:"text",placeholder:"Search by Korean Name...",value:e,onChange:e=>{const r=e.target.value.toLowerCase();t(r),m(c,r,s)}}),(0,i.jsxs)("select",{value:s,onChange:t=>{const s=t.target.value;n(s),m(c,e,s)},children:[(0,i.jsx)("option",{value:"all",children:"All Markets"}),(0,i.jsx)("option",{value:"krw",children:"KRW Market"}),(0,i.jsx)("option",{value:"btc",children:"BTC Market"}),(0,i.jsx)("option",{value:"usdt",children:"USDT Market"})]})]})]}),(0,i.jsxs)("div",{className:"market-lists",children:[(0,i.jsxs)("div",{className:"market-list",children:[(0,i.jsxs)("h3",{children:["KRW Market : ",c.krwList.length," \ud56d\ubaa9"]}),c.krwList.map(((e,t)=>(0,i.jsxs)("div",{className:`market-item ${e.priceChangeClass}`,children:[(0,i.jsxs)("div",{className:"market-name",children:[e.korean_name," (",e.english_name,")"]}),(0,i.jsx)("div",{className:"market-code",children:e.market}),(0,i.jsx)("div",{className:"market-price",children:e.price?parseInt(e.price).toLocaleString()+" \uc6d0":"Price not available"})]},t)))]}),(0,i.jsxs)("div",{className:"market-list",children:[(0,i.jsxs)("h3",{children:["BTC Market : ",c.btcList.length," \ud56d\ubaa9"]}),c.btcList.map(((e,t)=>(0,i.jsxs)("div",{className:"market-item",children:[(0,i.jsxs)("div",{className:"market-name",children:[e.korean_name," (",e.english_name,")"]}),(0,i.jsx)("div",{className:"market-code",children:e.market}),(0,i.jsxs)("div",{className:`market-price ${e.priceChangeClass}`,children:[e.price?e.price+" \uc6d0":"Price not available",(0,i.jsx)("div",{style:{fontSize:"10px",color:"midnightblue"},children:"\ud658\uc0b0\uac00\uaca9"})]})]},t)))]}),(0,i.jsxs)("div",{className:"market-list",children:[(0,i.jsxs)("h3",{children:["USDT Market : ",c.usdtList.length," \ud56d\ubaa9"]}),c.usdtList.map(((e,t)=>(0,i.jsxs)("div",{className:"market-item",children:[(0,i.jsxs)("div",{className:"market-name",children:[e.korean_name," (",e.english_name,")"]}),(0,i.jsx)("div",{className:"market-code",children:e.market}),(0,i.jsx)("div",{className:`market-price ${e.priceChangeClass}`,children:e.price?e.price.toLocaleString():"Price not available"})]},t)))]})]})]})}}}]);
//# sourceMappingURL=401.ad0a7d92.chunk.js.map