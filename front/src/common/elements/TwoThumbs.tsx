
import React from 'react'
import Slider from '@material-ui/core/Slider';
import { makeStyles } from '@material-ui/core';

interface Props {
  callback: (ages: [number?, number?]) => void
}

export default function TwoThumbs(props: Props) { 
    const marks = [
        {
          value: 17,
          label: '17',
        },
        {
          value: 99,
          label: '99'
        }
    ]
    
    const [value, setValue] = React.useState([17, 99]);
  
    const handleChange = (event: any, newValue: any) => {
      setValue(newValue);
    };

    const handleCommited = (event: any, newValue: any) => {
        let [minAge, maxAge] = newValue as [number?, number?]
        if (minAge === 17) {
          minAge = undefined
        }
        if (maxAge === 99) {
          maxAge = undefined
        }
        props.callback([minAge, maxAge])
    };
  
    const rootStyles = makeStyles({
        root: {
            color: 'green',
        }
    })

    const classes = rootStyles();

    return (
        <Slider
          classes={classes}
          value={value}
          onChange={handleChange}
          onChangeCommitted={handleCommited}
          valueLabelDisplay="auto"
          aria-labelledby="range-slider"
          min={17}
          max={99}
          marks={marks}
        />
    );
  
}