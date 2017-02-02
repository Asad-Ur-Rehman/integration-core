# Integration Provider Factory

This library is responsible for making the various integration providers available to consumers that require them.

# Public API
`listProviders`: Returns an array of JSON that contain the provider id, and a description of the fields required to authenticate to the providers target system.

`getProvider(providerId: String)`: Returns a specific integration provider that implements the `com.signalvine.integration.provider.IntegrationProvider` trait.

The return types are defined and managed in the [Integration Provider project](https://github.com/signalvine/integration-core)
