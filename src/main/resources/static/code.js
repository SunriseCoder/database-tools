let data;
let idCounter = 1;

function getNextId() {
    let number = idCounter++;
    return 'id' + number;
}

function onload() {
    let dumpName = getCookieParam('dump-name');
    document.getElementById("inputDumpName").value = dumpName;
}

function getCookieParam(key) {
    let params = [];

    let paramPairs = document.cookie.split('; ');
    paramPairs.forEach((pair) => {
        let splittedPair = pair.split('=');
        params[splittedPair[0]] = splittedPair[1];
    });

    return params[key];
}

function load() {
    setStatus('Loading...');
    document.getElementById("dataNode").innerHTML = '';

    // Saving Cookie
    let dumpName = document.getElementById("inputDumpName").value;
    document.cookie = 'dump-name=' + dumpName;

    // Fetching Data
    fetch('/rest/load?dump-name=' + dumpName)
        .then(response => {
            setStatus('Fetching...');
            if (response.status === 200) {
                response.json()
                    .then(json => {
                        data = json;
                        setSuccess('Dump: ' + data.name
                                + ', Tables: ' + data.tables.length
                                + ', FKs: ' + data.foreignKeys.length);
                        prepareData();
                        renderData();
                    }).catch(error => {
                        setError('Parsing error: ' + error);
                    });
            } else {
                setError(response.status + ": " + response.statusText);
            }
        }).catch(error => {
            setError('Loading error: ' + error);
        });
}

function prepareData() {
    data.tableMap = [];
    data.foreignKeyMapBySourceTable = [];
    data.foreignKeyMapByTargetTable = [];

    data.tables.forEach((table) => {
        data.tableMap[table.name] = table;
    });

    data.foreignKeys.forEach((foreignKey) => {
        if (data.foreignKeyMapBySourceTable[foreignKey.sourceTable] === undefined) {
            data.foreignKeyMapBySourceTable[foreignKey.sourceTable] = [];
        }
        data.foreignKeyMapBySourceTable[foreignKey.sourceTable].push(foreignKey);

        if (data.foreignKeyMapByTargetTable[foreignKey.targetTable] === undefined) {
            data.foreignKeyMapByTargetTable[foreignKey.targetTable] = [];
        }
        data.foreignKeyMapByTargetTable[foreignKey.targetTable].push(foreignKey);
    });
}

function renderData() {
    let dataNode = document.getElementById('dataNode');

    data.tables.forEach((table) => {
        renderTable(table, dataNode);
    });
}

function renderTable(table, parentNode) {
    let tableName = table.name;

    let leftSubTableDivId = getNextId();
    let rightSubTableDivId = getNextId();

    let tableContainerDiv = createElementWithClass('div', 'tableContainer', parentNode);

    // Left SubTableDiv
    createElementWithIdAndClass('div', leftSubTableDivId, 'subTableDiv', tableContainerDiv);

    // Table Container
    let tableDiv = createElementWithClass('div', 'tableDiv', tableContainerDiv);
    let tableElement = createElementWithClass('table', 'tableTable', tableDiv);
    let tr = createElement('tr', tableElement);
    let td = createElementWithClassAndText('td', 'tableName', tableName, tr);
    td.colSpan = 2;

    // Table Columns
    table.columns.forEach((column) => {
        renderColumn(column, tableElement);
    });

    // Incoming Foreign Keys
    let incomingFKs = data.foreignKeyMapByTargetTable[tableName];
    if (incomingFKs !== undefined) {
        renderIncomingFKs(incomingFKs, rightSubTableDivId, tableElement);
    }

    // Outgoing Foreign Keys
    let outgoingFKs = data.foreignKeyMapBySourceTable[tableName];
    if (outgoingFKs !== undefined) {
        renderOutgoingFKs(outgoingFKs, leftSubTableDivId, tableElement);
    }

    // Right SubTableDiv
    createElementWithIdAndClass('div', rightSubTableDivId, 'subTableDiv', tableContainerDiv);
}

function renderColumn(column, tableElement) {
    let tr = createElement('tr', tableElement);
    createElementWithText('td', column.name, tr);
    let columnTypeAndSizeText = column.type + '(' + column.size + ')';
    createElementWithText('td', columnTypeAndSizeText, tr);
}

function renderIncomingFKs(incomingFKs, subTableDivId, tableElement) {
    let firstRow = true;
    incomingFKs.forEach((fk) => {
        let tr = createElement('tr', tableElement);
        let td = createElement('td', tr);
        td.colSpan = 2;
        if (firstRow) {
            td.style.borderTopStyle = 'dashed';
        }

        // Foreign Key Text
        let fkText = fk.sourceTable + '.' + fk.sourceColumn + ' -> ' + fk.targetColumn + ' ';
        let textNode = document.createTextNode(fkText);
        td.append(textNode);

        // Button - Foreign Key Render Incoming Table
        let button = createElementWithText('button', '>>>', td);
        button.onclick = () => renderSubTable(fk.sourceTable, subTableDivId);

        firstRow = false;
    });
}

function renderOutgoingFKs(outgoingFKs, subTableDivId, tableElement) {
    let firstRow = true;
    outgoingFKs.forEach((fk) => {
        let tr = createElement('tr', tableElement);
        let td = createElement('td', tr);
        td.colSpan = 2;
        if (firstRow) {
            td.style.borderTopStyle = 'dashed';
        }

        // Button - Foreign Key Render Outgoing Table
        let button = createElementWithText('button', '<<<', td);
        button.onclick = () => renderSubTable(fk.targetTable, subTableDivId);

        // Foreign Key Column
        let fkText = ' ' + fk.sourceColumn + ' -> ' + fk.targetTable + '.' + fk.targetColumn;
        let textNode = document.createTextNode(fkText);
        td.append(textNode);

        firstRow = false;
    });
}

function renderSubTable(tableName, parentDivId) {
    let parentNode = document.getElementById(parentDivId);
    let table = data.tableMap[tableName];
    renderTable(table, parentNode);
}

function createElementWithIdAndClass(tag, id, cl, parent) {
    let element = createElement(tag, parent);
    element.id = id;
    element.classList.add(cl);
    return element;
}

function createElementWithClassAndText(tag, cl, text, parent) {
    let element = createElement(tag, parent);
    element.classList.add(cl);
    element.innerText = text;
    return element;
}

function createElementWithClass(tag, cl, parent) {
    let element = createElement(tag, parent);
    element.classList.add(cl);
    return element;
}

function createElementWithText(tag, text, parent) {
    let element = createElement(tag, parent);
    element.innerText = text;
    return element;
}

function createElement(tag, parent) {
    let element = document.createElement(tag);
    parent.appendChild(element);
    return element;
}

function setStatus(message) {
    let statusElement = document.getElementById("labelStatus");
    statusElement.innerText = message;
    statusElement.classList.remove("success", "error");
}

function setSuccess(message) {
    let statusElement = document.getElementById("labelStatus");
    statusElement.innerText = message;
    statusElement.classList.remove("error");
    statusElement.classList.add("success");
}

function setError(message) {
    let statusElement = document.getElementById("labelStatus");
    statusElement.innerText = message;
    statusElement.classList.remove("success");
    statusElement.classList.add("error");
}
