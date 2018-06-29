# Open Cisterna Binding

**This is alpha software.**

This binding uses an Open Cisterna sensor (see [Github repository](https://github.com/slotbaer/open-cisterna)) for providing fluid level information for a cistern.

## Supported Things

There is exactly one supported thing, which represents the Open Cisterna sensor. It has the id `cistern`.

## Thing Configuration

There are the following configuration parameters 

| Name    | Meaning                                  | Allows Values | Default Value |
|---------|------------------------------------------|---------------|---------------|
| refresh | The refresh interval in seconds          | > 0           | 900           |
| baseUrl | The base URL of the Open Cisterna sensor | Any valid URL | N/A           |

## Channels

The information that is retrieved from the Open Cisterna sensor is available as these channels:

| Channel Type ID | Item Type            | Description                                         |
|-----------------|----------------------|-----------------------------------------------------|
| fluidLevel      | Number:Dimensionless | The current fluid level as a fraction of 1.         |
| quantity        | Number:Dimensionless | The amount of fluid available in the cistern in m². |

## Full Example

demo.things:

```
opencisterna:cistern:default [ baseUrl="http://opencisterna:80", refresh=15 ]
```

demo.items:

```
Number Cistern_Fluid_Level      "Cistern Fluid Level [%.2f %%]"         { channel="opencisterna:cistern:default:fluidLevel" }
Number Cistern_Fluid_Quantity   "Cistern Fluid Quantity [%.3f m³]"      { channel="opencisterna:cistern:default:fluidQuantity" }

```

demo.sitemap:

```
sitemap demo label="Main Menu"
{
    Frame {
        Text item=Cistern_Fluid_Level
        Text item=Cistern_Fluid_Quantity
    }
}
```
