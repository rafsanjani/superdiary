There are two flavors of this app. `demo` and `standard`.

`demo` as the name suggests, uses fake data and is primarily used for instrumentation tests and
generating baseline profiles. No network connection is made in this flavor. A default username and
password can be used to login to grant access to stock diary entries created from a regular list.
In the `androidApp:benchmark` module, this flavor is referenced to be used during baseline profile
generation and benchmarking

`standard` is the main flavor which connects to the internet and performs all the standard functions
