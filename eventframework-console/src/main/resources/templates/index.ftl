<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="utf-8">
    <title>事件关系图</title>
</head>
<body>
<div id="main" style="width: 100%; height: 800px;"></div>
</body>
<script src="/static/plugin/echarts-4.2.1/echarts.min.js"></script>
<script type="application/javascript">

    var eventDataList = ${eventRelationListJson!"[]"};

    var nodes = [];
    var links = [];

    var nodesMap = {};

    eventDataList.forEach(function (node) {
        if (!node.eventCode) {
            // 跳过脏数据
            return;
        }

        nodesMap[node.eventCode] = {
            name: node.eventCode,
            category: '事件'
        };

        if (!!node.sourceEventCode) {
            links.push({
                source: node.sourceConsumerCode,
                target: node.eventCode,
                label: {
                    formatter: '消费事件 "' + node.sourceEventCode + '" 并生产新事件'
                }
            });
        } else if (node.producerCode) {

            nodesMap[node.producerCode] = {
                name: node.producerCode,
                category: '生产者'
            };

            links.push({
                source: node.producerCode,
                target: node.eventCode,
                label: {
                    formatter: '生产'
                }
            });
        } else if (node.consumerCode) {
            nodesMap[node.consumerCode] = {
                name: node.consumerCode,
                category: '消费者'
            };

            links.push({
                source: node.eventCode,
                target: node.consumerCode,
                label: {
                    formatter: '消费'
                }
            });
        }
    });

    // map 转 数组，用于去重
    for(var k in nodesMap) {
        nodes.push(nodesMap[k]);
    }

    // echarts 图表配置项
    var option = {
        title: {
            text: '事件关系图'
        },
        tooltip: {},
        legend: [{
            data: ["事件", "生产者", "消费者"]
        }],
        // animationDurationUpdate: 1500,
        // animationEasingUpdate: 'quinticInOut',
        series: [
            {
                // 图表类型：关系图
                type: 'graph',
                // 布局方式： 力图
                layout: 'force',

                // 是否开启鼠标缩放和平移漫游
                roam: true,
                // 鼠标漫游缩放时节点的相应缩放比例，当设为0时节点不随着鼠标的缩放而缩放
                // nodeScaleRatio: 0,
                // 节点是否可以拖拽
                draggable: true,
                // 是否在鼠标移到节点上的时候突出显示节点以及节点的边和邻接节点
                focusNodeAdjacency: true,
                // 关系图节点标记的图形
                // symbol: 'circle',
                // 节点大小
                symbolSize: 5,
                // 图形上的文本标签，可用于说明图形的一些数据信息
                label: {
                    show: true,
                    fontSize: 12
                },
                // 边两端的标记类型，可以是一个数组分别指定两端，也可以是单个统一指定。默认不显示标记
                edgeSymbol: ['circle', 'arrow'],
                // 边的标签
                edgeLabel: {
                    show: true,
                    fontSize: 12
                },
                // 节点样式
                itemStyle: {
                    normal: {
                        borderColor: '#fff',
                        borderWidth: 1,
                        shadowBlur: 10,
                        shadowColor: 'rgba(0, 0, 0, 0.3)'
                    }
                },
                // 关系边的公用线条样式
                lineStyle: {
                    color: 'rgba(0, 0, 0, 0.3)'
                },
                // 重点，当鼠标放到节点上时的样式
                emphasis: {
                    lineStyle: {
                        width: 5,
                        color: 'rgba(0, 0, 0, 0.8)'
                    }
                },
                // 节点分类的类目
                categories: [{
                    name: "事件",
                    symbol: 'circle'
                }, {
                    name: "生产者",
                    symbol: 'roundRect'
                }, {
                    name: "消费者",
                    symbol: 'rect'
                }],
                // 节点数据
                data: nodes,
                // 节点间的关系数据
                links: links,
                // 鼠标移上去后的提示
                tooltip: {
                    formatter: function (params, ticket, callback) {
                        if(params.dataType === 'node') {
                            // 节点
                            return params.data.category + ": " + params.data.name;
                        } else if(params.dataType === 'edge') {
                            // 边
                            return params.data.label.formatter + ": " + params.data.source + " > " + params.data.target;
                        }
                        return params.name;
                    }
                }
            }
        ]
    };

    echarts.init(document.getElementById('main')).setOption(option);

</script>
</html>
