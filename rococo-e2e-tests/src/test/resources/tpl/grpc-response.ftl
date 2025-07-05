<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpResponseAttachment" -->
<head>
<meta charset="UTF-8">
    <title>gRPC Response: ${data.name}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/highlight.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/styles/github.min.css">
    <script>hljs.highlightAll();</script>
    <style>
        body { padding: 20px; font-family: Arial, sans-serif; }
        pre { white-space: pre-wrap; background: #f8f9fa; padding: 10px; border-radius: 4px; }
.grpc-status-ok { color: #198754; font-weight: bold; }
.grpc-status-error { color: #dc3545; font-weight: bold; }
.metadata-item { margin-bottom: 5px; }
    </style>
</head>
<body>
<div class="container">
    <h3>gRPC Response</h3>

    <div class="card mb-3">
        <div class="card-header">Status</div>
        <div class="card-body">
            <#if data.headers?? && data.headers['grpc-status']??>
                <#if data.headers['grpc-status'] == "0">
                    <span class="grpc-status-ok">OK (code: 0)</span>
                <#else>
                    <span class="grpc-status-error">
                        ERROR (code: ${data.headers['grpc-status']}
                        <#if data.headers['grpc-message']??>
                            - ${data.headers['grpc-message']}
                        </#if>
                    </span>
                </#if>
            <#else>
                <span>Unknown status</span>
            </#if>
        </div>
    </div>

    <#if (data.headers)?has_content>
    <div class="card mb-3">
        <div class="card-header">Response Metadata</div>
        <div class="card-body">
            <#list data.headers as name, value>
                <#if !name?starts_with("grpc-")>
                    <div class="metadata-item">
                        <pre><code><b>${name}</b>: ${value}</code></pre>
                    </div>
                </#if>
            </#list>
        </div>
    </div>
    </#if>

    <#if data.body??>
    <div class="card mb-3">
        <div class="card-header">Response Message</div>
        <div class="card-body">
            <pre><code class="language-json">${data.body}</code></pre>
        </div>
    </div>
    </#if>

    <#if data.headers?? && data.headers['grpc-trailers']??>
    <div class="card mb-3">
        <div class="card-header">Trailers</div>
        <div class="card-body">
            <pre><code>${data.headers['grpc-trailers']}</code></pre>
        </div>
    </div>
    </#if>
</div>
</body>
</html>