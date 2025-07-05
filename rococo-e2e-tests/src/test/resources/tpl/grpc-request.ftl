<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpRequestAttachment" -->
<head>
<meta charset="UTF-8">
    <title>gRPC Request: ${data.name}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/highlight.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/styles/github.min.css">
    <script>hljs.highlightAll();</script>
    <style>
        body { padding: 20px; font-family: Arial, sans-serif; }
        pre { white-space: pre-wrap; background: #f8f9fa; padding: 10px; border-radius: 4px; }
.grpc-method { font-weight: bold; color: #0d6efd; }
.metadata-item { margin-bottom: 5px; }
    </style>
</head>
<body>
<div class="container">
    <h3>gRPC Request</h3>

    <div class="card mb-3">
        <div class="card-header">Method</div>
        <div class="card-body">
            <pre><code class="grpc-method">${data.url}</code></pre>
        </div>
    </div>

    <#if (data.headers)?has_content>
    <div class="card mb-3">
        <div class="card-header">Metadata</div>
        <div class="card-body">
            <#list data.headers as name, value>
                <div class="metadata-item">
                    <pre><code><b>${name}</b>: ${value}</code></pre>
                </div>
            </#list>
        </div>
    </div>
    </#if>

    <#if data.body??>
    <div class="card mb-3">
        <div class="card-header">Request Message</div>
        <div class="card-body">
            <pre><code class="language-json">${data.body}</code></pre>
        </div>
    </div>
    </#if>
</div>
</body>
</html>