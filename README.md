# Open Cisterna Binding

**This is alpha software.**

This binding uses an Open Cisterna sensor for providing fluid level information for a cistern.

## Supported Things

There is exactly one supported thing, which represents the Open Cisterna sensor. It has the id `cistern`.

## Thing Configuration

There is only one supported configuration parameter called ```refresh``` which defines the refresh interval in seconds.

## Channels

The information that is retrieved from the Open Cisterna sensor is available as these channels:

| Channel Type ID | Item Type            | Description                                         |
|-----------------|----------------------|-----------------------------------------------------|
| level           | Number:Dimensionless | The current fluid level in %.                       |
| quantity        | Number:Dimensionless | The amount of fluid available in the cistern in m². |

## Full Example

demo.things:

```
opencisterna:cistern:main [ refresh=300 ]
```

demo.items:

```
Number Fluid_Level  "Fluid Level [%.1f]" { channel="opencisterna:cistern:main:level" }
Number Fluid_Quantity  "Fluid Quantity [%.1f m²]" { channel="opencisterna:cistern:main:quantity" }
```

demo.sitemap:

```
sitemap demo label="Main Menu"
{
    Frame {
        Text item=Fluid_Level
        Text item=Fluid_Quantity
    }
}
```
