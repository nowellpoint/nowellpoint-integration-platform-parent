    <div class="card">
        <div class="card-body">
            <dl class="dl-vertical">
                <h5 class="card-title">${labels['details']}</h5>
                <dt>${labels['name']}</dt>
                <dd>${organization.name}</dd>
                <dt>${labels['account.number']}</dt>
                <dd>${organization.number}</dd>
                <dt>${labels['domain']}</dt>
                <dd>${organization.domain}</dd>
                <dt>${labels['created.on']}</dt>
                <dd>${organization.createdOn?date?string.long} - ${organization.createdOn?time?string.medium}</dd>
            </dl>
        </div>
    </div>