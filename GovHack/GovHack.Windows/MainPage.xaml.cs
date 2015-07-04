using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;
using Windows.Devices.Geolocation;
using Bing.Maps;


// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace GovHack
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public MainPage()
        {
            this.InitializeComponent();

        }

        protected override async void OnNavigatedTo(NavigationEventArgs e)
        {
            Geolocator geolocator = new Geolocator();
            var geo = await geolocator.GetGeopositionAsync();

            MapControl.Center = new Location(geo.Coordinate.Latitude, geo.Coordinate.Longitude);
            MapControl.ZoomLevel = 15;

            Pushpin pushpin = new Pushpin();
            pushpin.Text = "1";

            MapLayer.SetPosition(pushpin, new Location(geo.Coordinate.Latitude, geo.Coordinate.Longitude));

            AddPushpin(new Location(geo.Coordinate.Latitude, geo.Coordinate.Longitude), "Seattle","Hello", DataLayer, "W");
        }


        private void CloseInfobox_Tapped(object sender, Windows.UI.Xaml.Input.TappedRoutedEventArgs e)
        {
            Infobox.Visibility = Visibility.Collapsed;
        }


        public void AddPushpin(Location latlong, string title, string description, MapLayer layer, string pushpinText=null)
        {
            Pushpin p = new Pushpin()
            {
                Tag = new Metadata()
                {
                    Title = title,
                    Description = description
                }
            };

            MapLayer.SetPosition(p, latlong);

            if (pushpinText != null)
            {
                p.Text = pushpinText;
            }

            p.Tapped += PinTapped;

            layer.Children.Add(p);
        }

        public class Metadata
        {
            public string Title { get; set; }
            public string Description { get; set; }
        }


        private void PinTapped(object sender, Windows.UI.Xaml.Input.TappedRoutedEventArgs e)
        {
            Pushpin p = sender as Pushpin;
            Metadata m = (Metadata)p.Tag;

            //Ensure there is content to be displayed before modifying the infobox control
            if (!String.IsNullOrEmpty(m.Title) || !String.IsNullOrEmpty(m.Description))
            {
                Infobox.DataContext = m;

                Infobox.Visibility = Visibility.Visible;

                MapLayer.SetPosition(Infobox, MapLayer.GetPosition(p));
            }
            else
            {
                Infobox.Visibility = Visibility.Collapsed;
            }
        }
    }
}
